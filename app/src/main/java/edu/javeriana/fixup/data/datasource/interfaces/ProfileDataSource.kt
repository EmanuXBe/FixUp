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
    suspend fun updateProfileData(name: String, email: String, phone: String, address: String, profileImageUrl: String?)
    suspend fun getUserData(userId: String): Map<String, Any>?
}
