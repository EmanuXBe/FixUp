package edu.javeriana.fixup.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor encargado de inyectar el token JWT de Firebase en las peticiones de Retrofit.
 * 
 * Este interceptor recupera el token del usuario actual de Firebase de forma síncrona
 * (dentro del hilo de red de OkHttp) y lo añade al encabezado 'Authorization'.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val auth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = auth.currentUser

        // Si no hay usuario, enviamos la petición original (ej. registro o login si aplicara)
        if (user == null) {
            return chain.proceed(originalRequest)
        }

        return try {
            // Obtenemos el token JWT de forma síncrona
            // forceRefresh = false para usar el token en caché si es válido
            val task = user.getIdToken(false)
            val tokenResult = Tasks.await(task)
            val token = tokenResult.token

            if (token != null) {
                // Añadimos el token al encabezado Authorization: Bearer <token>
                val authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(authenticatedRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } catch (e: Exception) {
            // En caso de error obteniendo el token, procedemos con la petición original
            // o podrías lanzar una excepción si el backend requiere el token obligatoriamente.
            chain.proceed(originalRequest)
        }
    }
}
