package edu.javeriana.fixup.data.datasource.impl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.interfaces.ProfileDataSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Implementación de ProfileDataSource usando Firebase Auth, Storage y Firestore.
 */
class ProfileDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
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
}
