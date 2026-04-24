package edu.javeriana.fixup.data.datasource.impl

import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

/**
 * Implementación de ReviewDataSource que consume datos de la API Express (PostgreSQL).
 */
class ReviewExpressDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService
) : ReviewDataSource {

    override suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>> {
        return try {
            val response = apiService.getUserReviews(userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener reseñas del usuario: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviewsByServiceId(serviceId: String): Result<List<ReviewModel>> {
        return try {
            val id = serviceId.toIntOrNull() ?: return Result.failure(Exception("ID de servicio inválido"))
            val reviews = apiService.getReviewsByServiceId(id)
            Result.success(reviews.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReview(review: ReviewModel): Result<Boolean> {
        return try {
            val request = ReviewRequestDto(
                userId = review.userId,
                serviceId = review.serviceId,
                rating = review.rating,
                comment = review.comment
            )
            apiService.createReview(request)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addLike(reviewId: String, userId: String): Result<Unit> {
        // Implementar si la API de Express soporta likes
        return Result.success(Unit)
    }

    override suspend fun removeLike(reviewId: String, userId: String): Result<Unit> {
        // Implementar si la API de Express soporta likes
        return Result.success(Unit)
    }

    override suspend fun getLikedUsers(reviewId: String): Result<List<String>> {
        // Implementar si la API de Express soporta likes
        return Result.success(emptyList())
    }
}
