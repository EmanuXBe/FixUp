package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.ProfileDataSource
import edu.javeriana.fixup.data.network.dto.ReviewRequestDto
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.ui.model.ReviewModel
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

    override suspend fun getReviewsByUserId(userId: String): List<ReviewModel> {
        /**
         * Obtiene las reseñas filtradas por el ID del usuario.
         * Se maneja la respuesta de Retrofit dentro de un bloque try-catch para capturar errores de red.
         * El uso de .map { it.toDomain() } garantiza que la capa de datos no exponga DTOs a las capas superiores.
         */
        return try {
            val response = apiService.getUserReviews(userId)
            if (response.isSuccessful) {
                // Si la respuesta es exitosa (200 OK), mapeamos los DTOs a modelos de dominio
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                // Manejo explícito de errores del servidor para facilitar la depuración académica
                val errorBody = response.errorBody()?.string()
                throw Exception("Error del servidor (${response.code()}): ${errorBody ?: "Sin mensaje"}")
            }
        } catch (e: Exception) {
            // Propagamos la excepción para ser capturada en el Repositorio
            throw e
        }
    }

    override suspend fun createReview(review: ReviewModel): ReviewModel {
        /**
         * Envía una nueva reseña al backend.
         * Convierte el modelo de dominio a un objeto de solicitud (ReviewRequestDto).
         */
        val request = ReviewRequestDto(
            userId = review.userId,
            serviceId = "1", // El ID del servicio está hardcoded por ahora según la lógica de negocio actual
            rating = review.rating,
            comment = review.comment
        )
        val resultDto = apiService.createReview(request)
        return resultDto.toDomain()
    }

    override suspend fun updateReview(id: String, review: ReviewModel): ReviewModel {
        /**
         * Actualiza una reseña existente.
         */
        val request = ReviewRequestDto(
            userId = review.userId,
            serviceId = "1",
            rating = review.rating,
            comment = review.comment
        )
        val resultDto = apiService.updateReview(id, request)
        return resultDto.toDomain()
    }

    override suspend fun deleteReview(id: String) {
        apiService.deleteReview(id)
    }
}
