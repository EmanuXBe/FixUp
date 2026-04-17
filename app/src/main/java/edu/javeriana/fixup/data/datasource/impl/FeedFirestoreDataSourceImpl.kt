package edu.javeriana.fixup.data.datasource.impl


import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.data.network.dto.CategoryDto
import edu.javeriana.fixup.data.network.dto.PublicationDto
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FeedFirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FeedDataSource {

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
        return snapshot.documents.mapNotNull { it.toPublicationDto() }
    }

    override suspend fun getPublicationById(id: Int): PublicationDto {
        // Los documentos en Firestore usan el id como string (ej: "1", "2")
        val snapshot = firestore.collection("articles").document(id.toString()).get().await()
        return snapshot.toPublicationDto() ?: throw Exception("Artículo no encontrado")
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
            "imageUrl" to downloadUrl
        )
        docRef.set(data).await()
        return property.copy(imageUrl = downloadUrl)
    }

    override suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewModel> {
        val snapshot = firestore.collection("reviews")
            .whereEqualTo("serviceId", serviceId.toString())
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            ReviewModel(
                userId = doc.getString("userId") ?: "",
                rating = (doc.getLong("rating") ?: 0L).toInt(),
                comment = doc.getString("comment") ?: "",
                userName = doc.getString("userName") ?: "Usuario"
            )
        }
    }

    override suspend fun createReview(review: ReviewRequestDto): ReviewModel {
        val docRef = firestore.collection("reviews").document()
        val data = mapOf(
            "userId" to review.userId,
            "serviceId" to review.serviceId,
            "rating" to review.rating,
            "comment" to review.comment,
            "userName" to "Usuario",
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        docRef.set(data).await()
        return ReviewModel(
            userId = review.userId,
            rating = review.rating,
            comment = review.comment,
            userName = "Usuario"
        )
    }

    private fun DocumentSnapshot.toPublicationDto(): PublicationDto? {
        val title = getString("title") ?: return null
        val price = getDouble("price") ?: 0.0
        return PublicationDto(
            id = this.id,
            title = title,
            priceText = "Desde $$price",
            description = getString("description"),
            location = getString("category"),
            imageUrl = getString("imageUrl")
        )
    }
}