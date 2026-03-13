package edu.javeriana.fixup.data.repository

import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.AuthDataSource

/**
 * Repositorio de autenticación.
 * Actúa como intermediario entre el DataSource (Firebase) y los ViewModels.
 * Aquí se puede añadir lógica de negocio adicional (validaciones, transformaciones, etc.)
 */
class AuthRepository(
    private val dataSource: AuthDataSource = AuthDataSource()
) {

    /** Retorna el usuario actual si hay sesión activa. */
    val currentUser: FirebaseUser?
        get() = dataSource.currentUser

    /** Indica si hay un usuario autenticado actualmente. */
    val isUserLoggedIn: Boolean
        get() = dataSource.currentUser != null

    /**
     * Inicia sesión con email y contraseña.
     * Lanza excepción si las credenciales son incorrectas.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser {
        return dataSource.signIn(email, password)
    }

    /**
     * Registra un nuevo usuario.
     * Lanza excepción si el email ya existe u otro error ocurre.
     */
    suspend fun signUp(email: String, password: String): FirebaseUser {
        return dataSource.signUp(email, password)
    }

    /** Cierra la sesión activa. */
    fun signOut() {
        dataSource.signOut()
    }
}