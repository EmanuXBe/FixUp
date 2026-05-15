package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

class RentRepository @Inject constructor(
    private val dataSource: RentDataSource,
    private val apiService: FixUpApiService,
    private val authRepository: AuthRepository
) {
    suspend fun getProperties(): Result<List<PropertyModel>> {
        return try {
            val properties = dataSource.getRentProperties()
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPropertyById(id: String): Result<PropertyModel> {
        return try {
            Result.success(dataSource.getPropertyById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Publica un nuevo inmueble delegando al DataSource la subida de imágenes y
     * la llamada al backend.
     *
     * ¿Por qué el Repository obtiene el userId aquí?
     * El ViewModel no debería acceder directamente a AuthRepository; el Repository
     * actúa como orquestador que reúne las dependencias necesarias de múltiples
     * fuentes (Auth + Rent) para completar la operación.
     *
     * @return Result.success con el propertyId generado, o Result.failure con el error.
     */
    suspend fun createProperty(
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: Double,
        tipo: String,
        imageUris: List<Uri>
    ): Result<String> {
        // Verificar que el usuario esté autenticado antes de intentar la operación
        val userId = authRepository.currentUser?.uid
            ?: return Result.failure(Exception("Debes iniciar sesión para publicar un inmueble."))

        return try {
            val propertyId = dataSource.createProperty(
                userId      = userId,
                titulo      = titulo,
                ubicacion   = ubicacion,
                descripcion = descripcion,
                precio      = precio,
                tipo        = tipo,
                imageUris   = imageUris
            )
            Result.success(propertyId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsByServiceId(serviceId: Int): Result<List<ReviewModel>> {
        return try {
            val reviewDtos = apiService.getReviewsByServiceId(serviceId)
            val reviews = reviewDtos.map { dto ->
                ReviewModel(
                    id = dto.id ?: "",
                    rating = dto.rating ?: 0,
                    comment = dto.comment ?: "",
                    date = dto.date ?: "",
                    authorName = dto.authorName ?: dto.user?.name ?: "Usuario ${dto.user?.id ?: ""}",
                    authorProfileImageUrl = dto.authorProfileImageUrl ?: dto.user?.profileImage ?: "",
                    serviceTitle = dto.service?.title ?: ""
                )
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createReview(serviceId: Int, rating: Int, comment: String): Result<ReviewModel> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val request = ReviewRequestDto(
                userId = uid,
                serviceId = serviceId.toString(),
                rating = rating,
                comment = comment
            )
            val resultDto = apiService.createReview(request)
            val savedReview = ReviewModel(
                id = resultDto.id ?: "",
                rating = resultDto.rating ?: 0,
                comment = resultDto.comment ?: "",
                date = resultDto.date ?: "",
                authorName = resultDto.authorName ?: resultDto.user?.name ?: "Usuario",
                authorProfileImageUrl = resultDto.authorProfileImageUrl ?: resultDto.user?.profileImage ?: "",
                serviceTitle = resultDto.service?.title ?: ""
            )
            Result.success(savedReview)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<ReviewModel> {
        val uid = authRepository.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val request = ReviewRequestDto(
                userId = uid,
                serviceId = "0", // No es necesario para update en el backend generalmente, pero se envía por el DTO
                rating = rating,
                comment = comment
            )
            val resultDto = apiService.updateReview(reviewId, request)
            val updatedReview = ReviewModel(
                id = resultDto.id ?: "",
                rating = resultDto.rating ?: 0,
                comment = resultDto.comment ?: "",
                date = resultDto.date ?: "",
                authorName = resultDto.authorName ?: resultDto.user?.name ?: "Usuario",
                authorProfileImageUrl = resultDto.authorProfileImageUrl ?: resultDto.user?.profileImage ?: "",
                serviceTitle = resultDto.service?.title ?: ""
            )
            Result.success(updatedReview)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            apiService.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
