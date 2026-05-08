package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.data.network.dto.CategoryDto
import edu.javeriana.fixup.data.network.dto.PublicationDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FeedDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
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
        // Try Firestore first (populated by DataSeeder in debug, or real services in prod)
        val snapshot = firestore.collection("services").get().await()
        if (snapshot.documents.isNotEmpty()) {
            return snapshot.documents.mapNotNull { doc ->
                PublicationDto(
                    id        = doc.id,
                    title     = doc.getString("title") ?: "Sin título",
                    priceText = "Desde $${doc.getDouble("price")?.toLong() ?: 0}",
                    description = doc.getString("description"),
                    location  = doc.getString("category") ?: "",
                    imageUrl  = doc.getString("imageUrl"),
                    authorId  = doc.getString("providerId")
                )
            }
        }
        // Fallback: backend (PostgreSQL) for release environments without Firestore services
        return apiService.getServices().map { it.toPublicationDto() }
    }

    override suspend fun getFollowingPublications(followingIds: List<String>): List<PublicationDto> {
        if (followingIds.isEmpty()) return emptyList()
        return getPublications().filter { it.authorId in followingIds }
    }

    override suspend fun getPublicationById(id: String): PublicationDto {
        val doc = firestore.collection("services").document(id).get().await()
        if (doc.exists()) {
            return PublicationDto(
                id        = doc.id,
                title     = doc.getString("title") ?: "Sin título",
                priceText = "Desde $${doc.getDouble("price")?.toLong() ?: 0}",
                description = doc.getString("description"),
                location  = doc.getString("category") ?: "",
                imageUrl  = doc.getString("imageUrl"),
                authorId  = doc.getString("providerId")
            )
        }
        return apiService.getServiceById(id.toIntOrNull() ?: 0).toPublicationDto()
    }

    override suspend fun createPublication(property: PropertyModel, imageUri: Uri): PropertyModel {
        // 1. Subir imagen a Firebase Storage
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("publications/$filename.jpg")
        
        ref.putFile(imageUri).await()
        
        // 2. Obtener la URL de descarga
        val downloadUrl = ref.downloadUrl.await().toString()
        
        // 3. Crear el objeto con la URL de la imagen y enviarlo a la API
        val publicationWithImage = property.copy(imageUrl = downloadUrl)
        
        val dto = ServiceDto(
            id = publicationWithImage.id,
            title = publicationWithImage.title,
            description = publicationWithImage.description,
            price = publicationWithImage.price,
            category = publicationWithImage.location,
            imageUrl = publicationWithImage.imageUrl
        )
        
        val resultDto = apiService.createService(dto)
        return PropertyModel(
            id = resultDto.id,
            title = resultDto.title,
            description = resultDto.description,
            price = resultDto.price,
            location = resultDto.category,
            imageUrl = resultDto.imageUrl
        )
    }

    // Also handle ServiceDto to PublicationDto if needed, as getServices returns ServiceDto
    private fun ServiceDto.toPublicationDto() = PublicationDto(
        id = this.id.toString(),
        title = this.title ?: "Sin título",
        priceText = "Desde $${this.price ?: 0.0}",
        description = this.description,
        location = this.category ?: "",
        imageUrl = this.imageUrl,
        authorId = this.providerId
    )
}
