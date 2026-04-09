package edu.javeriana.fixup.data.datasource

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
}
