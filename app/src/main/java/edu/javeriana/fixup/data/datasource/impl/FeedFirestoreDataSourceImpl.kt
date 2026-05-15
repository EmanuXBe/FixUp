package edu.javeriana.fixup.data.datasource.impl


import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.data.network.dto.CategoryDto
import edu.javeriana.fixup.data.network.dto.PublicationDto
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FeedFirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : FeedDataSource {

    private val TAG_FEED = "FeedFirestoreDataSource"

    override suspend fun getCategories(): List<CategoryDto> {
        return listOf(
            CategoryDto(1, "Baños", R.drawable.bano),
            CategoryDto(2, "Iluminación", R.drawable.luz),
            CategoryDto(3, "Cocina", R.drawable.cocina),
            CategoryDto(4, "Exterior", R.drawable.exterior)
        )
    }

    override suspend fun getPublications(): List<PublicationDto> {
        val snapshot = firestore.collection("articles").get().await()
        Log.d("FeedDataSource", "Recuperados ${snapshot.size()} documentos de 'articles'")
        val publications = snapshot.documents.map { it.toPublicationDto() }
        Log.d("FeedDataSource", "Total publicaciones: ${publications.size}")
        return publications
    }

    override suspend fun getFollowingPublications(followingIds: List<String>): List<PublicationDto> {
        if (followingIds.isEmpty()) return emptyList()
        // Firestore whereIn limit is 10 — chunk and merge results
        return followingIds.chunked(10).flatMap { chunk ->
            firestore.collection("articles")
                .whereIn("authorId", chunk)
                .get()
                .await()
                .documents
                .map { it.toPublicationDto() }
        }
    }

    override suspend fun getPublicationById(id: String): PublicationDto {
        val snapshot = firestore.collection("articles").document(id).get().await()
        if (!snapshot.exists()) throw Exception("Artículo no encontrado: $id")
        return snapshot.toPublicationDto()
    }

    override suspend fun getRecentPublications(): List<PublicationDto> {
        val currentUserId = auth.currentUser?.uid
        val collectionName = "properties"
        Log.d(TAG_FEED, "getRecentPublications(): consultando colección '$collectionName'")

        val snapshot = firestore.collection(collectionName).get().await()
        val total = snapshot.size()
        Log.d(TAG_FEED, "Firestore devolvió $total documentos en '$collectionName'")

        var mapFailures = 0
        var locationFailures = 0
        var createdAtFailures = 0

        val dtos = snapshot.documents.mapNotNull { doc ->
            try {
                val dto = doc.toPublicationDto()
                if (dto.latitude == null || dto.longitude == null) {
                    locationFailures++
                    Log.e(
                        TAG_FEED,
                        "Doc ${doc.id}: no se pudieron extraer coordenadas (location/latitude/longitude)"
                    )
                }
                if (dto.createdAt == null) {
                    createdAtFailures++
                    Log.e(TAG_FEED, "Doc ${doc.id}: no se pudo extraer createdAt")
                }
                if (currentUserId != null) {
                    try {
                        val likeDoc = doc.reference.collection("likes")
                            .document(currentUserId).get().await()
                        dto.copy(likedByCurrentUser = likeDoc.exists())
                    } catch (e: Exception) {
                        Log.e(TAG_FEED, "Doc ${doc.id}: error leyendo likes — ${e.message}")
                        dto
                    }
                } else {
                    dto
                }
            } catch (e: Exception) {
                mapFailures++
                Log.e(TAG_FEED, "Doc ${doc.id}: excepción al mapear DTO — ${e.message}", e)
                null
            }
        }

        Log.d(
            TAG_FEED,
            "Resumen mapeo → total: $total | mapeoFallido: $mapFailures | " +
                    "sinCoords: $locationFailures | sinCreatedAt: $createdAtFailures | " +
                    "dtosEmitidos: ${dtos.size}"
        )
        return dtos
    }

    override suspend fun togglePublicationLike(publicationId: String, userId: String, liked: Boolean) {
        val ref = firestore.collection("properties").document(publicationId)
        val likeRef = ref.collection("likes").document(userId)
        firestore.runTransaction { tx ->
            if (liked) {
                tx.set(likeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                tx.update(ref, "likeCount", FieldValue.increment(1))
            } else {
                tx.delete(likeRef)
                tx.update(ref, "likeCount", FieldValue.increment(-1))
            }
        }.await()
    }

    override suspend fun createPublication(property: PropertyModel, imageUri: Uri): PropertyModel {
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("publications/$filename.jpg")
        ref.putFile(imageUri).await()
        val downloadUrl = ref.downloadUrl.await().toString()

        val docRef = firestore.collection("articles").document()
        val data = mapOf(
            "title" to property.title,
            "description" to property.description,
            "price" to property.price,
            "category" to property.location,
            "imageUrl" to downloadUrl,
            "authorId" to auth.currentUser?.uid,
            "createdAt" to FieldValue.serverTimestamp(),
            "likeCount" to 0
        )
        docRef.set(data).await()
        return property.copy(imageUrl = downloadUrl)
    }

    private fun DocumentSnapshot.toPublicationDto(): PublicationDto {
        val title = getString("title")
            ?: getString("titulo")
            ?: getString("name")
            ?: getString("nombre")
            ?: "Sin título"
        val price = getDouble("precio") ?: getDouble("price")
            ?: getLong("precio")?.toDouble() ?: getLong("price")?.toDouble() ?: 0.0
        val imageUrl = (get("imagenes") as? List<*>)?.firstOrNull() as? String
            ?: getString("imageUrl") ?: getString("imageurl") ?: getString("image")

        // El campo `location` puede llegar como GeoPoint (Firestore SDK) o como
        // mapa serializado con `_latitude`/`_longitude`. Si no, se cae al par plano
        // `latitude`/`longitude` de la raíz del documento.
        val geoPoint = try { getGeoPoint("location") } catch (_: Exception) { null }
        val locationMap = get("location") as? Map<*, *>
        val lat = geoPoint?.latitude
            ?: (locationMap?.get("_latitude") as? Number)?.toDouble()
            ?: (locationMap?.get("latitude") as? Number)?.toDouble()
            ?: getDouble("latitude")
        val lng = geoPoint?.longitude
            ?: (locationMap?.get("_longitude") as? Number)?.toDouble()
            ?: (locationMap?.get("longitude") as? Number)?.toDouble()
            ?: getDouble("longitude")

        val locationText = (get("ubicacion") as? String)
            ?: (get("category") as? String)
            ?: (get("categoria") as? String)
            ?: (get("location") as? String) // sólo si vino como texto

        val createdAtMillis: Long? = getTimestamp("createdAt")?.toDate()?.time
            ?: getLong("createdAt")
            ?: getString("createdAt")?.let { iso ->
                try { java.time.Instant.parse(iso).toEpochMilli() } catch (_: Exception) { null }
            }

        return PublicationDto(
            id = this.id,
            title = title,
            priceText = "Desde $$price",
            description = getString("descripcion") ?: getString("description"),
            location = locationText,
            imageUrl = imageUrl,
            authorId = getString("authorId") ?: getString("userId"),
            latitude = lat,
            longitude = lng,
            createdAt = createdAtMillis,
            likeCount = getLong("likeCount")?.toInt() ?: 0
        )
    }
}