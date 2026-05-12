package edu.javeriana.fixup.data.datasource.impl


import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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

    private fun DocumentSnapshot.toPublicationDto(): PublicationDto {
        // Intentamos variantes del campo título en español e inglés
        val title = getString("title")
            ?: getString("titulo")
            ?: getString("name")
            ?: getString("nombre")
            ?: "Sin título"
        val price = getDouble("price") ?: getLong("price")?.toDouble() ?: 0.0
        val imageUrl = getString("imageUrl") ?: getString("imageurl") ?: getString("image")

        return PublicationDto(
            id = this.id,
            title = title,
            priceText = "Desde $$price",
            description = getString("description") ?: getString("descripcion"),
            location = getString("category") ?: getString("categoria") ?: getString("location"),
            imageUrl = imageUrl,
            authorId = getString("authorId") ?: getString("userId")
        )
    }
}