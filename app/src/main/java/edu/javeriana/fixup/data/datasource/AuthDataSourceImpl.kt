package edu.javeriana.fixup.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación de AuthDataSource usando Firebase.
 */
class AuthDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthDataSource {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Error al iniciar sesión")
    }

    override suspend fun signUp(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Error al registrar el usuario")
    }

    override fun signOut() {
        auth.signOut()
    }
}
