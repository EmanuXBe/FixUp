package edu.javeriana.fixup.data.repository

import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.AuthDataSource
import javax.inject.Inject

/**
 * Repositorio de autenticación.
 * Actúa como intermediario entre el DataSource (Firebase) y los ViewModels.
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
     * Inicia sesión con email y contraseña.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser {
        return dataSource.signIn(email, password)
    }

    /**
     * Registra un nuevo usuario.
     */
    suspend fun signUp(email: String, password: String): FirebaseUser {
        return dataSource.signUp(email, password)
    }

    /** Cierra la sesión activa. */
    fun signOut() {
        dataSource.signOut()
    }
}