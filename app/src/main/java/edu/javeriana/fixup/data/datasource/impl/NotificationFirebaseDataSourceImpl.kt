package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.javeriana.fixup.data.datasource.interfaces.NotificationDataSource
import edu.javeriana.fixup.data.network.dto.NotificationDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationFirebaseDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationDataSource {

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
                                actionType = doc.getString("actionType")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(Result.success(notifications))
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveNotification(userId: String, notification: NotificationDto): Result<Unit> {
        return try {
            val notificationMap = hashMapOf(
                "title" to notification.title,
                "message" to notification.message,
                "date" to notification.date,
                "isRead" to notification.isRead,
                "profileImageUrl" to notification.profileImageUrl,
                "previewImageUrl" to notification.previewImageUrl,
                "actionType" to notification.actionType
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
