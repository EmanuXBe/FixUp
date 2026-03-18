package edu.javeriana.fixup.ui.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onCedulaChanged(cedula: String) {
        _uiState.update { it.copy(cedula = cedula) }
    }

    fun onRoleSelected(role: String) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Registra un nuevo usuario consumiendo el Result del repositorio.
     */
    fun signUp(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val cedula = _uiState.value.cedula.trim()

        if (email.isBlank() || password.isBlank() || cedula.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.signUp(email, password)
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }.onFailure { e ->
                val errorMessage = when {
                    e.message?.contains("email") == true &&
                            e.message?.contains("already") == true -> "Este correo ya está registrado"
                    e.message?.contains("network") == true -> "Error de conexión. Verifica tu internet"
                    e.message?.contains("badly formatted") == true -> "El formato del correo no es válido"
                    else -> "Error al registrarse. Intenta de nuevo"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }
}
