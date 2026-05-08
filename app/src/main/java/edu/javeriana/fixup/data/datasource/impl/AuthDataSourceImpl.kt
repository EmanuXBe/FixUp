package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.AuthDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación de AuthDataSource usando Firebase.
 */
class AuthDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthDataSource {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("No se pudo obtener el usuario después del inicio de sesión")
    }

    override suspend fun signUp(email: String, password: String, cedula: String, role: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("No se pudo obtener el usuario después del registro")

        // Guardar información adicional en Firestore
        val userData = mapOf(
            "uid" to user.uid,
            "email" to email,
            "cedula" to cedula,
            "role" to role,
            "name" to email.substringBefore("@"),
            "phone" to cedula,
            "createdAt" to Timestamp.now()
        )

        firestore.collection("users").document(user.uid).set(userData).await()

        return user
    }

    override suspend fun signOut() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                // Borrar el token de FCM en Firestore antes de cerrar sesión
                firestore.collection("users").document(userId)
                    .update("fcmToken", null)
                    .await()
            } catch (e: Exception) {
                // Si falla (ej. por reglas de seguridad), cerramos sesión igualmente
            }
        }
        auth.signOut()
    }
}
