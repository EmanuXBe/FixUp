package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Implementación de FeedDataSource que consume datos del backend real y usa Firebase Storage.
 */
class FeedDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService,
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
        val services = apiService.getServices()
        return services.map { it.toDto() }
    }

    override suspend fun getPublicationById(id: Int): PublicationDto {
        val service = apiService.getServiceById(id)
        return service.toDto()
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
        
        return apiService.createService(publicationWithImage)
    }

    override suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewModel> {
        return try {
            apiService.getReviewsByServiceId(serviceId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createReview(review: ReviewRequest): ReviewModel {
        return apiService.createReview(review)
    }

    private fun PropertyModel.toDto() = PublicationDto(
        id = this.id.toString(),
        title = this.title ?: "Sin título",
        priceText = "Desde $${this.price ?: 0.0}",
        description = this.description,
        location = this.location,
        imageUrl = this.imageUrl
    )
}
