package edu.javeriana.fixup.data.datasource


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Capa de datos: se conecta directamente con Firebase Authentication.
 * Solo contiene funciones suspend que llaman a la API de Firebase.
 */
class AuthDataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /** Retorna el usuario actualmente autenticado, o null si no hay sesión. */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Inicia sesión con email y contraseña.
     * @throws Exception si las credenciales son incorrectas.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Error al iniciar sesión")
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     * @throws Exception si el email ya está en uso u otro error ocurre.
     */
    suspend fun signUp(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Error al registrar el usuario")
    }

    /** Cierra la sesión del usuario actual. */
    fun signOut() {
        auth.signOut()
    }
}