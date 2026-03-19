package edu.javeriana.fixup.ui.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogInUiState())
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onErrorDismissed() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Llama a Firebase para iniciar sesión.
     */
    fun signIn(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                authRepository.signIn(email, password)
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("password") == true ||
                            e.message?.contains("credential") == true ||
                            e.message?.contains("no user") == true -> "Correo o contraseña incorrectos"
                    e.message?.contains("network") == true -> "Error de conexión. Verifica tu internet"
                    else -> "Error al iniciar sesión. Intenta de nuevo"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
            }
        }
    }
}