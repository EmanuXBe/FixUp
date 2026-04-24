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
            
            // 2. Actualizar el perfil del usuario y sincronizar reseñas usando el nuevo flujo batch
            val user = profileDataSource.getCurrentUser() ?: throw Exception("Usuario no autenticado")
            val data = profileDataSource.getUserData(user.uid) ?: emptyMap()
            
            profileDataSource.updateProfileData(
                name = data["name"] as? String ?: user.displayName ?: "Usuario",
                email = user.email ?: "",
                phone = data["phone"] as? String ?: "",
                address = data["address"] as? String ?: "",
                profileImageUrl = downloadUrl
            )
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    /**
     * Actualiza los datos del perfil en Auth y Firestore con actualización en lote para reseñas.
     */
    suspend fun updateProfileData(name: String, email: String, phone: String, address: String): Result<Unit> {
        return try {
            profileDataSource.updateProfileData(name, email, phone, address, null)
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
     * Crea una reseña usando el userId dinámico.
     */
    suspend fun createReview(userId: String, rating: Int, comment: String): Result<edu.javeriana.fixup.ui.model.ReviewModel> {
        return try {
            val user = currentUser
            val reviewToSave = edu.javeriana.fixup.ui.model.ReviewModel(
                rating = rating,
                comment = comment,
                authorName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Usuario",
                authorProfileImageUrl = user?.photoUrl?.toString() ?: "",
                date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val savedReview = profileDataSource.createReview(reviewToSave)
            Result.success(savedReview)
        } catch (e: Exception) {
            Result.failure(e.toAppError())
        }
    }

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<Unit> {
        return try {
            profileDataSource.updateReview(reviewId, comment, rating)
            Result.success(Unit)
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

    suspend fun getFollowCounts(userId: String): Result<Pair<Int, Int>> {
        return try {
            val data = profileDataSource.getUserData(userId)

            // El "as? Long" es porque Firebase guarda los números como Long
            // Usamos ?: 0 para que si el campo NO EXISTE en Firebase, no explote y devuelva 0
            val followers = (data?.get("followersCount") as? Long)?.toInt() ?: 0
            val following = (data?.get("followingCount") as? Long)?.toInt() ?: 0

            Result.success(Pair(followers, following))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
