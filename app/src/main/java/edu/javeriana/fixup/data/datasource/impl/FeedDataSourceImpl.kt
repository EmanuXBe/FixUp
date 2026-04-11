package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.data.network.dto.CategoryDto
import edu.javeriana.fixup.data.network.dto.PublicationDto
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.data.network.api.FixUpApiService
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
        return services.map { it.toPublicationDto() }
    }

    override suspend fun getPublicationById(id: Int): PublicationDto {
        val service = apiService.getServiceById(id)
        return service.toPublicationDto()
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

    override suspend fun getReviewsByServiceId(serviceId: Int): List<ReviewModel> {
        return try {
            apiService.getReviewsByServiceId(serviceId).map { dto ->
                ReviewModel(
                    userId = dto.userId.toString(),
                    rating = dto.rating?.toInt() ?: 0,
                    comment = dto.comment ?: "",
                    userName = dto.userName ?: "Usuario ${dto.userId}"
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("FeedDataSource", "Error fetching reviews", e)
            emptyList()
        }
    }

    override suspend fun createReview(review: ReviewRequestDto): ReviewModel {
        val resultDto = apiService.createReview(review)
        return ReviewModel(
            userId = resultDto.userId.toString(),
            rating = resultDto.rating?.toInt() ?: 0,
            comment = resultDto.comment ?: "",
            userName = resultDto.userName ?: "Usuario ${resultDto.userId}"
        )
    }

    // Also handle ServiceDto to PublicationDto if needed, as getServices returns ServiceDto
    private fun ServiceDto.toPublicationDto() = PublicationDto(
        id = this.id.toString(),
        title = this.title ?: "Sin título",
        priceText = "Desde $${this.price ?: 0.0}",
        description = this.description,
        location = this.category ?: "",
        imageUrl = this.imageUrl
    )
}
