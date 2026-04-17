package edu.javeriana.fixup.data.repository

import com.google.firebase.auth.FirebaseUser
import edu.javeriana.fixup.data.datasource.interfaces.AuthDataSource
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.UserDto
import edu.javeriana.fixup.data.util.toAppError
import javax.inject.Inject

/**
 * Repositorio de autenticación refactorizado con manejo de Result y errores personalizados.
 * 
 * Responsabilidad: Coordinar la autenticación local/Firebase y la sincronización 
 * con el backend centralizado en PostgreSQL.
 */
class AuthRepository @Inject constructor(
    private val dataSource: AuthDataSource,
    private val apiService: FixUpApiService // Inyectamos el servicio de Retrofit para sincronización
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
            // Mapeamos el error a uno amigable de nuestra app
            Result.failure(e.toAppError())
        }
    }

    /**
     * Registra un nuevo usuario retornando un Result.
     */
    suspend fun signUp(email: String, password: String, cedula: String, role: String): Result<FirebaseUser> {
        return try {
            val user = dataSource.signUp(email, password, cedula, role)
            Result.success(user)
        } catch (e: Exception) {
            // Mapeamos el error a uno amigable de nuestra app
            Result.failure(e.toAppError())
        }
    }

    /**
     * Sincroniza los datos del usuario recién registrado con el backend en Render.
     * Este paso es crucial para la integridad referencial en PostgreSQL, ya que
     * vincula el Firebase UID con el resto de la lógica de negocio (servicios, reseñas, etc).
     */
    suspend fun syncUserToBackend(userDto: UserDto): Result<UserDto> {
        return try {
            val response = apiService.createUser(userDto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra la sesión activa. */
    fun signOut() {
        dataSource.signOut()
    }
}