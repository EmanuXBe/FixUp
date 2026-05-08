package edu.javeriana.fixup.ui.features.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: edu.javeriana.fixup.data.repository.UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogInUiState())
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.matches(emailRegex)
    }

    /**
     * Llama al repositorio (que ahora devuelve Result) para iniciar sesión.
     */
    fun signIn(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Validaciones previas
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        if (!isEmailValid(email)) {
            _uiState.update { it.copy(error = "El formato del correo no es válido") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.signIn(email, password)
            
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()

                // Obtener y registrar el token de FCM - NON-BLOCKING
                viewModelScope.launch {
                    try {
                        val token = FirebaseMessaging.getInstance().token.await()
                        userRepository.updateFcmToken(user.uid, token)
                    } catch (e: Exception) {
                        Log.e("LogInViewModel", "Error al obtener token FCM", e)
                    }
                }
            }.onFailure { e ->
                // Usamos directamente el mensaje que viene del repositorio (mapeado por AppError)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
