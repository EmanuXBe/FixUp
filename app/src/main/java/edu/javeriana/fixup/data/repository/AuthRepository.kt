package edu.javeriana.fixup.data.repository

import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.AuthDataSource
import javax.inject.Inject

/**
 * Repositorio de autenticación refactorizado con manejo de Result.
 */
class AuthRepository @Inject constructor(
    private val dataSource: AuthDataSource
) {

    /** Retorna el usuario actual si hay sesión activa. */
    val currentUser: FirebaseUser?
        get() = dataSource.currentUser

    /** Indica si hay un usuario autenticado actualmente. */
    val isUserLoggedIn: Boolean
        get() = dataSource.currentUser != null

    /**
     * Inicia sesión con email y contraseña retornando un Result.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = dataSource.signIn(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario retornando un Result.
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = dataSource.signUp(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra la sesión activa. */
    fun signOut() {
        dataSource.signOut()
    }
}
