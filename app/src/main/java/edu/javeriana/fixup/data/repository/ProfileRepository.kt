package edu.javeriana.fixup.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.ProfileDataSource
import edu.javeriana.fixup.data.util.toAppError

class ProfileRepository(
    private val profileDataSource: ProfileDataSource = ProfileDataSource()
) {
    /** Retorna el usuario actual de Firebase. */
    val currentUser: FirebaseUser?
        get() = profileDataSource.getCurrentUser()

    /**
     * Sube una imagen y actualiza el perfil del usuario.
     * Retorna Result.success con la URL o Result.failure con el error mapeado.
     */
    suspend fun updateProfileImage(uri: Uri): Result<String> {
        return try {
            // 1. Subir imagen a Storage
            val downloadUrl = profileDataSource.uploadProfileImage(uri)
            
            // 2. Actualizar el perfil del usuario con esa URL
            profileDataSource.updateProfilePhotoUrl(downloadUrl)
            
            // 3. Retornar éxito
            Result.success(downloadUrl)
        } catch (e: Exception) {
            // 4. Retornar fallo mapeado
            Result.failure(e.toAppError())
        }
    }
}
