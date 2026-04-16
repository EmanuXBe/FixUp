package edu.javeriana.fixup.data.datasource.interfaces

import com.google.firebase.auth.FirebaseUser

/**
 * Contrato para el Data Source de Autenticación.
 */
interface AuthDataSource {
    val currentUser: FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser
    suspend fun signUp(email: String, password: String, cedula: String, role: String): FirebaseUser
    fun signOut()
}
