package edu.javeriana.fixup.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.ProfileDataSource
import edu.javeriana.fixup.data.util.toAppError
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDataSource: ProfileDataSource
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

    suspend fun getReviewsByUserId(userId: String): Result<List<edu.javeriana.fixup.ui.model.ReviewModel>> {
        return try {
            // Ignoramos el userId dinámico y usamos siempre "1" para Retrofit
            val reviews = profileDataSource.getReviewsByUserId("1")
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    /**
     * Crea una reseña usando siempre el userId "1" según requerimiento.
     */
    suspend fun createReview(rating: Int, comment: String): Result<edu.javeriana.fixup.ui.model.ReviewModel> {
        return try {
            val user = currentUser
            val reviewToSave = edu.javeriana.fixup.ui.model.ReviewModel(
                userId = "1", // ID fijo quemado
                userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Usuario Prueba",
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
}
