package edu.javeriana.fixup.data.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Clase sellada para representar errores conocidos de la aplicación.
 */
sealed class AppError(message: String) : Exception(message) {
    object NetworkError : AppError("No hay conexión a internet. Verifica tu red.")
    object TimeoutError : AppError("La operación tardó demasiado. Reintenta pronto.")
    object InvalidCredentials : AppError("Correo o contraseña incorrectos.")
    object UserNotFound : AppError("El usuario no existe.")
    object UserAlreadyExists : AppError("Este correo ya está registrado.")
    data class UnknownError(val originalMessage: String?) : AppError(originalMessage ?: "Ocurrió un error inesperado.")
}

/**
 * Utilidad para mapear excepciones de Firebase a errores de nuestra app.
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is FirebaseNetworkException -> AppError.NetworkError
        is TimeoutCancellationException -> AppError.TimeoutError
        is FirebaseAuthInvalidCredentialsException -> AppError.InvalidCredentials
        is FirebaseAuthInvalidUserException -> AppError.UserNotFound
        is FirebaseAuthUserCollisionException -> AppError.UserAlreadyExists
        else -> AppError.UnknownError(this.message)
    }
}
