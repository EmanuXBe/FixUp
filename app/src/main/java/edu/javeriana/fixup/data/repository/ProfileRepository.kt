package edu.javeriana.fixup.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.ProfileDataSource
import edu.javeriana.fixup.data.util.toAppError
import edu.javeriana.fixup.ui.model.ReviewModel
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource
) {
    val currentUser: FirebaseUser?
        get() = profileDataSource.getCurrentUser()

    suspend fun updateProfileImage(uri: Uri): Result<String> {
        return try {
            val downloadUrl = profileDataSource.uploadProfileImage(uri)
            profileDataSource.updateProfilePhotoUrl(downloadUrl)
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun getReviewsByUserId(userId: String): Result<List<ReviewModel>> {
        return try {
            val reviews = profileDataSource.getReviewsByUserId("1")
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun createReview(rating: Int, comment: String): Result<ReviewModel> {
        return try {
            val reviewToSave = ReviewModel(
                userId = 1,
                serviceId = 1,
                rating = rating,
                comment = comment,
                date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val savedReview = profileDataSource.createReview(reviewToSave)
            Result.success(savedReview)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<ReviewModel> {
        return try {
            val reviewToUpdate = ReviewModel(
                id = reviewId.toIntOrNull() ?: 0,
                userId = 1,
                serviceId = 1,
                rating = rating,
                comment = comment
            )
            val updatedReview = profileDataSource.updateReview(reviewId, reviewToUpdate)
            Result.success(updatedReview)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            profileDataSource.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }
}
