package edu.javeriana.fixup.data.datasource.impl


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
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
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
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

    override suspend fun getFollowingPublications(followingIds: List<String>): List<PublicationDto> {
        if (followingIds.isEmpty()) return emptyList()
        
        // Firestore 'in' query supports up to 10 elements. 
        // For a real app, we might need to chunk this or use a different approach.
        // But for this requirement, we'll use the 'in' operator.
        val snapshot = firestore.collection("articles")
            .whereIn("authorId", followingIds)
            .get()
            .await()
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
            "imageUrl" to downloadUrl,
            "authorId" to auth.currentUser?.uid
        )
        docRef.set(data).await()
        return property.copy(imageUrl = downloadUrl)
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
            imageUrl = getString("imageUrl"),
            authorId = getString("authorId")
        )
    }
}