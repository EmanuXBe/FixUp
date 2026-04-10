package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
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
            photoUri = Uri.parse(photoUrl)
        }
        user.updateProfile(profileUpdates).await()
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun getReviewsByUserId(userId: String): List<edu.javeriana.fixup.ui.model.ReviewModel> {
        return try {
            // Se asume que el backend ya maneja el userId "1" si se pasa como parámetro
            apiService.getReviewsByUserId(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createReview(review: edu.javeriana.fixup.ui.model.ReviewModel): edu.javeriana.fixup.ui.model.ReviewModel {
        val request = ReviewRequest(
            userId = review.userId,
            serviceId = 1, // Valor por defecto para asegurar compatibilidad
            rating = review.rating,
            comment = review.comment
        )
        return apiService.createReview(request)
    }

    override suspend fun updateReview(id: String, review: edu.javeriana.fixup.ui.model.ReviewModel): edu.javeriana.fixup.ui.model.ReviewModel {
        val request = ReviewRequest(
            userId = review.userId,
            serviceId = 1,
            rating = review.rating,
            comment = review.comment
        )
        return apiService.updateReview(id, request)
    }

    override suspend fun deleteReview(id: String) {
        apiService.deleteReview(id)
    }
}
