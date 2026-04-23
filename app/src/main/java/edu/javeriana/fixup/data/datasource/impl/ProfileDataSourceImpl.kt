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

    override suspend fun updateProfileData(name: String, email: String, phone: String, address: String, profileImageUrl: String?) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
        
        // 1. Actualizar Display Name y Email en Firebase Auth
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            profileImageUrl?.let { photoUri = Uri.parse(it) }
        }
        user.updateProfile(profileUpdates).await()
        
        // Actualizar email si es diferente
        if (user.email != email) {
            user.updateEmail(email).await()
        }

        // 2. Preparar el Batch de Firestore
        val batch = firestore.batch()

        // 2a. Actualizar datos en la colección "users"
        val userRef = firestore.collection("users").document(user.uid)
        val userUpdates = mutableMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "address" to address
        )
        profileImageUrl?.let { userUpdates["profileImageUrl"] = it }
        batch.update(userRef, userUpdates as Map<String, Any>)

        // 2b. Buscar y actualizar todas las reseñas del usuario para mantener la consistencia
        val reviewsSnapshot = firestore.collection("reviews")
            .whereEqualTo("userId", user.uid)
            .get()
            .await()

        for (reviewDoc in reviewsSnapshot.documents) {
            val reviewUpdate = mutableMapOf<String, Any>(
                "authorName" to name
            )
            profileImageUrl?.let { reviewUpdate["authorProfileImageUrl"] = it }
            batch.update(reviewDoc.reference, reviewUpdate)
        }

        // 3. Ejecutar actualización atómica
        batch.commit().await()
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
                id = doc.id,
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

    override suspend fun updateReview(reviewId: String, newComment: String, newRating: Int) {
        val updates = mapOf(
            "comment" to newComment,
            "rating" to newRating
        )
        firestore.collection("reviews").document(reviewId).update(updates).await()
    }

    override suspend fun deleteReview(reviewId: String) {
        firestore.collection("reviews").document(reviewId).delete().await()
    }
}
