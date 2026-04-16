package edu.javeriana.fixup.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.interfaces.ProfileDataSource
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

    /**
     * Actualiza los datos del perfil en Auth y Firestore.
     */
    suspend fun updateProfileData(name: String, email: String, phone: String, address: String): Result<Unit> {
        return try {
            profileDataSource.updateProfileData(name, email, phone, address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    /**
     * Obtiene los datos adicionales del usuario desde Firestore.
     */
    suspend fun getUserData(userId: String): Result<Map<String, Any>?> {
        return try {
            val data = profileDataSource.getUserData(userId)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun getReviewsByUserId(userId: String): Result<List<edu.javeriana.fixup.ui.model.ReviewModel>> {
        return try {
            val reviews = profileDataSource.getReviewsByUserId(userId)
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

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<edu.javeriana.fixup.ui.model.ReviewModel> {
        return try {
            val reviewToUpdate = edu.javeriana.fixup.ui.model.ReviewModel(
                id = reviewId,
                userId = "1", // ID fijo quemado
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
