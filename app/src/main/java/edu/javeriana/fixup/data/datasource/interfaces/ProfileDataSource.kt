package edu.javeriana.fixup.data.datasource.interfaces

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

/**
 * Contrato del Data Source para Perfil.
 */
interface ProfileDataSource {
    suspend fun uploadProfileImage(uri: Uri, timeoutMillis: Long = 15000L): String
    suspend fun updateProfilePhotoUrl(photoUrl: String)
    fun getCurrentUser(): FirebaseUser?
    suspend fun getReviewsByUserId(userId: String): List<edu.javeriana.fixup.ui.model.ReviewModel>
    suspend fun createReview(review: edu.javeriana.fixup.ui.model.ReviewModel): edu.javeriana.fixup.ui.model.ReviewModel
    suspend fun updateReview(id: String, review: edu.javeriana.fixup.ui.model.ReviewModel): edu.javeriana.fixup.ui.model.ReviewModel
    suspend fun deleteReview(id: String)
    suspend fun updateProfileData(name: String, email: String, phone: String, address: String)
    suspend fun getUserData(userId: String): Map<String, Any>?
}
