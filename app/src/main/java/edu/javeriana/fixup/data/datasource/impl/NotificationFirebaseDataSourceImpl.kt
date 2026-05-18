package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.javeriana.fixup.data.datasource.interfaces.NotificationDataSource
import edu.javeriana.fixup.data.network.dto.NotificationDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationFirebaseDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationDataSource {

    // Caché simple en memoria para evitar lookups repetidos del mismo actor.
    private val profileImageCache = mutableMapOf<String, String?>()

    override fun getNotifications(userId: String): Flow<Result<List<NotificationDto>>> = callbackFlow {
        val subscription = firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            NotificationDto(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                message = doc.getString("message") ?: "",
                                date = doc.getString("date") ?: "",
                                isRead = doc.getBoolean("isRead") ?: false,
                                profileImageUrl = doc.getString("profileImageUrl"),
                                previewImageUrl = doc.getString("previewImageUrl"),
                                actionType = doc.getString("actionType"),
                                actorId = doc.getString("actorId")
                                    ?: doc.getString("likerId")
                                    ?: doc.getString("followerId")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    // Enriquecer con foto del actor para notificaciones que no la traen
                    launch {
                        val enriched = enrichWithActorProfiles(notifications)
                        trySend(Result.success(enriched))
                    }
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Para cada notificación cuya `profileImageUrl` esté vacía pero tenga `actorId`,
     * busca el perfil del actor en `/users/{actorId}` y rellena el campo. Usa una caché
     * en memoria y lookups en paralelo (`async`) para no degradar latencia con muchas
     * notificaciones del mismo usuario.
     */
    private suspend fun enrichWithActorProfiles(items: List<NotificationDto>): List<NotificationDto> = coroutineScope {
        val needsLookup = items.filter { it.profileImageUrl.isNullOrBlank() && !it.actorId.isNullOrBlank() }
        if (needsLookup.isEmpty()) return@coroutineScope items

        val uniqueActorIds = needsLookup.mapNotNull { it.actorId }.distinct()
        val lookups = uniqueActorIds.map { actorId ->
            async {
                actorId to (profileImageCache[actorId] ?: runCatching {
                    val doc = firestore.collection("users").document(actorId).get().await()
                    doc.getString("profileImageUrl") ?: doc.getString("photoUrl")
                }.getOrNull().also { profileImageCache[actorId] = it })
            }
        }
        val resolved = lookups.awaitAll().toMap()

        items.map { dto ->
            val photo = dto.actorId?.let { resolved[it] }
            if (!photo.isNullOrBlank() && dto.profileImageUrl.isNullOrBlank()) {
                dto.copy(profileImageUrl = photo)
            } else dto
        }
    }

    override suspend fun saveNotification(userId: String, notification: NotificationDto): Result<Unit> {
        return try {
            val notificationMap = hashMapOf<String, Any?>(
                "title" to notification.title,
                "message" to notification.message,
                "date" to notification.date,
                "isRead" to notification.isRead,
                "profileImageUrl" to notification.profileImageUrl,
                "previewImageUrl" to notification.previewImageUrl,
                "actionType" to notification.actionType,
                "actorId" to notification.actorId
            )
            firestore.collection("users")
                .document(userId)
                .collection("notifications")
                .add(notificationMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("notifications")
                .document(notificationId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
