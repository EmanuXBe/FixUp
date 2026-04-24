package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    private val authRepository: AuthRepository
) {
    fun getReviewsByUserId(userId: String): Flow<Result<List<ReviewModel>>> = flow {
        emit(reviewDataSource.getReviewsByUserId(userId))
    }

    fun getReviewsByServiceId(serviceId: String): Flow<Result<List<ReviewModel>>> = flow {
        emit(reviewDataSource.getReviewsByServiceId(serviceId))
    }

    suspend fun createReview(serviceId: String, serviceTitle: String, rating: Int, comment: String): Result<Boolean> {
        return try {
            val currentUserId = authRepository.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
            val review = ReviewModel(
                id = "",
                userId = currentUserId,
                serviceId = serviceId,
                serviceTitle = serviceTitle,
                rating = rating,
                comment = comment,
                authorName = authRepository.currentUser?.displayName ?: "Usuario",
                authorProfileImageUrl = authRepository.currentUser?.photoUrl?.toString() ?: "",
                date = "",
                likedBy = emptyList()
            )
            reviewDataSource.createReview(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(reviewId: String, isCurrentlyLiked: Boolean): Result<Unit> {
        // Por ahora, como toggleLikeReview no está en la interfaz ReviewDataSource, 
        // lo dejamos como success o implementamos si es necesario.
        return Result.success(Unit)
    }

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return Result.success(Unit)
    }
}
