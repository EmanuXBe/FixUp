package edu.javeriana.fixup.data.datasource.impl

import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementación de ReviewDataSource que consume datos de la API Express (PostgreSQL).
 */
class ReviewExpressDataSourceImpl @Inject constructor(
    private val apiService: FixUpApiService
) : ReviewDataSource {

    override fun getReviewsByUserId(userId: String): Flow<Result<List<ReviewModel>>> = flow {
        try {
            val response = apiService.getUserReviews(userId)
            if (response.isSuccessful) {
                emit(Result.success(response.body()?.map { it.toDomain() } ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Error al obtener reseñas del usuario: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getReviewsByServiceId(serviceId: String): Flow<Result<List<ReviewModel>>> = flow {
        try {
            val id = serviceId.toIntOrNull() ?: run {
                emit(Result.failure(Exception("ID de servicio inválido")))
                return@flow
            }
            val reviews = apiService.getReviewsByServiceId(id)
            emit(Result.success(reviews.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
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

    override suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<Unit> {
        return try {
            val request = ReviewRequestDto(
                rating = rating,
                comment = comment
            )
            apiService.updateReview(reviewId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            apiService.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
