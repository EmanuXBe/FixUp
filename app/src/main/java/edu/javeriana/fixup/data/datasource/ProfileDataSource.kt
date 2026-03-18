package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProfileDataSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /** Sube la imagen a Firebase Storage y retorna la URL de descarga. */
    suspend fun uploadProfileImage(uri: Uri): String {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
        
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    /** Actualiza la URL de la foto en el perfil de Firebase Auth. */
    suspend fun updateProfilePhotoUrl(photoUrl: String) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(photoUrl)
        }
        user.updateProfile(profileUpdates).await()
    }

    /** Retorna el usuario actual. */
    fun getCurrentUser() = auth.currentUser
}
