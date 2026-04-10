package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.ProfileDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.model.ReviewRequestDto
import edu.javeriana.fixup.ui.model.ReviewModel
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Implementación de ProfileDataSource usando Firebase Auth y Storage.
 */
class ProfileDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val apiService: FixUpApiService
) : ProfileDataSource {

    override suspend fun uploadProfileImage(uri: Uri, timeoutMillis: Long): String {
        return withTimeout(timeoutMillis) {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val storageRef = storage.reference.child("profilePictures/${user.uid}.jpg")
            
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        }
    }

    override suspend fun updateProfilePhotoUrl(photoUrl: String) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val profileUpdates = userProfileChangeRequest {
            photoUri = photoUrl.toUri()
        }
        user.updateProfile(profileUpdates).await()
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun getReviewsByUserId(userId: String): List<ReviewModel> {
        return try {
            apiService.getReviewsByUserId(userId).map { dto ->
                dto.toDomain()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun createReview(review: ReviewModel): ReviewModel {
        val request = ReviewRequestDto(
            userId = review.userId.toIntOrNull() ?: 1,
            serviceId = 1,
            rating = review.rating,
            comment = review.comment
        )
        return apiService.createReview(request).toDomain()
    }

    override suspend fun updateReview(id: String, review: ReviewModel): ReviewModel {
        val request = ReviewRequestDto(
            userId = review.userId.toIntOrNull() ?: 1,
            serviceId = 1,
            rating = review.rating,
            comment = review.comment
        )
        return apiService.updateReview(id, request).toDomain()
    }

    override suspend fun deleteReview(id: String) {
        apiService.deleteReview(id)
    }
}
