package edu.javeriana.fixup.data.repository

import android.net.Uri
import edu.javeriana.fixup.data.datasource.interfaces.RentDataSource
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.util.AppConstants
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

    suspend fun createProperty(property: PropertyModel, imageUri: Uri): Result<PropertyModel> {
        return try {
            val created = dataSource.createProperty(property, imageUri)
            Result.success(created)
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
