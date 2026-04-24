package edu.javeriana.fixup.data.datasource.interfaces
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.flow.Flow

interface ReviewDataSource {
    fun getReviewsByUserId(userId: String): Flow<Result<List<ReviewModel>>>
    fun getReviewsByServiceId(serviceId: String): Flow<Result<List<ReviewModel>>>
    suspend fun createReview(review: ReviewModel): Result<Boolean>
    suspend fun addLike(reviewId: String, userId: String): Result<Unit>
    suspend fun removeLike(reviewId: String, userId: String): Result<Unit>
    suspend fun getLikedUsers(reviewId: String): Result<List<String>>
    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
