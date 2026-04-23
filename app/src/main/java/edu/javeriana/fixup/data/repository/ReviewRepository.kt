package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.ReviewDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    private val authRepository: AuthRepository
) {
    fun getReviewsByUserId(userId: String): Flow<Result<List<ReviewModel>>> = flow {
        try {
            val reviewDtos = reviewDataSource.getReviewsByUserId(userId)
            emit(Result.success(reviewDtos.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createReview(articleId: String, articleName: String, rating: Int, comment: String): Result<Unit> {
        return try {
            val currentUserId = authRepository.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            val reviewDto = edu.javeriana.fixup.data.network.dto.ReviewDto(
                id = null,
                userId = currentUserId,
                articleId = articleId,
                articleName = articleName,
                rating = rating,
                comment = comment,
                date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                authorName = authRepository.currentUser?.displayName ?: "Usuario",
                authorProfileImageUrl = authRepository.currentUser?.photoUrl?.toString(),
                user = null,
                service = null
            )
            reviewDataSource.createReview(reviewDto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(reviewId: String, isCurrentlyLiked: Boolean): Result<Unit> {
        return try {
            val currentUserId = authRepository.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            reviewDataSource.toggleLikeReview(reviewId, currentUserId, isCurrentlyLiked)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
