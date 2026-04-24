package edu.javeriana.fixup.data.datasource.interfaces
import edu.javeriana.fixup.ui.model.ReviewModel

interface ReviewDataSource {
    suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>>
    suspend fun getReviewsByServiceId(serviceId: String): Result<List<ReviewModel>>
    suspend fun createReview(review: ReviewModel): Result<Boolean>
    suspend fun addLike(reviewId: String, userId: String): Result<Unit>
    suspend fun removeLike(reviewId: String, userId: String): Result<Unit>
    suspend fun getLikedUsers(reviewId: String): Result<List<String>>
}
