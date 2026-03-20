package edu.javeriana.fixup.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ProfileDataSource {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /** 
     * Sube la imagen a Firebase Storage con un tiempo límite realista.
     * @param timeoutMillis Tiempo máximo de espera (15 segundos).
     */
    suspend fun uploadProfileImage(uri: Uri, timeoutMillis: Long = 15000L): String {
        return withTimeout(timeoutMillis) {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val storageRef = storage.reference.child("profilePictures/${user.uid}.jpg")
            
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        }
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
