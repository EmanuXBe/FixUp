package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
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
 * Implementación de ProfileDataSource usando Firebase Auth, Storage y Firestore.
 */
class ProfileDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore,
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

    override suspend fun updateProfileData(name: String, email: String, phone: String, address: String) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        
        // 1. Actualizar Display Name y Email en Firebase Auth
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user.updateProfile(profileUpdates).await()
        
        // Actualizar email si es diferente
        if (user.email != email) {
            user.updateEmail(email).await()
        }

        // 2. Actualizar datos en Firestore
        val updates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "address" to address
        )
        firestore.collection("users").document(user.uid).update(updates).await()
    }

    override suspend fun getUserData(userId: String): Map<String, Any>? {
        return firestore.collection("users").document(userId).get().await().data
    }

    override suspend fun getReviewsByUserId(userId: String): List<ReviewModel> {
        val snapshot = firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val rating = doc.getLong("rating")?.toInt() ?: 0
            val comment = doc.getString("comment") ?: ""
            val authorName = doc.getString("authorName") ?: "Usuario"
            val authorProfileImageUrl = doc.getString("authorProfileImageUrl") ?: ""
            val serviceTitle = doc.getString("articleName") ?: doc.getString("serviceTitle") ?: ""
            val serviceId = doc.getString("serviceId") ?: doc.getString("articleId") ?: ""
            val createdAt = doc.getTimestamp("createdAt")
            val date = createdAt?.toDate()?.let { 
                java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
            } ?: ""

            ReviewModel(
                userId = userId,
                serviceId = serviceId,
                rating = rating,
                comment = comment,
                authorName = authorName,
                authorProfileImageUrl = authorProfileImageUrl,
                serviceTitle = serviceTitle,
                date = date
            )
        }
    }

    override suspend fun createReview(review: ReviewModel): ReviewModel {
        /**
         * Envía una nueva reseña al backend.
         * Convierte el modelo de dominio a un objeto de solicitud (ReviewRequestDto).
         */
        val request = ReviewRequestDto(
            userId = auth.currentUser?.uid ?: "",
            serviceId = review.serviceId, // Ahora dinámico
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
            userId = auth.currentUser?.uid ?: "",
            serviceId = review.serviceId, // Ahora dinámico
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
