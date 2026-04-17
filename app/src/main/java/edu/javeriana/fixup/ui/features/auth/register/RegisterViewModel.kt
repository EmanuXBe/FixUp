package edu.javeriana.fixup.ui.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.network.dto.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel encargado del registro de usuarios.
 * Implementa una lógica de registro en dos pasos: Firebase Auth y Backend (PostgreSQL).
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
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
     * Inicia el proceso de registro.
     * 1. Registra al usuario en Firebase Authentication.
     * 2. Si es exitoso, sincroniza los datos con el Backend en Render.
     * * Este enfoque garantiza que solo los usuarios autenticados existan en la DB relacional.
     */
    fun signUp(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val cedula = _uiState.value.cedula.trim()
        val role = _uiState.value.selectedRole

        // Validación básica de campos
        if (email.isBlank() || password.isBlank() || cedula.isBlank() || role.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos, incluyendo el rol") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            // Iniciamos estado de carga global
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // PASO 1: Registro en Firebase
            val authResult = authRepository.signUp(
                email = email,
                password = password
            )
            
            authResult.onSuccess { firebaseUser ->
                // PASO 2: Sincronización con el Backend (PostgreSQL)
                // Usamos el UID generado por Firebase como ID en nuestra base de datos
                val userDto = UserDto(
                    id = firebaseUser.uid,
                    name = email.substringBefore("@"), // Placeholder o nombre derivado
                    email = email,
                    phone = cedula, // Usamos la cédula como identificador de contacto
                    address = null,
                    role = role,
                    profileImageUrl = null
                )

                val syncResult = authRepository.syncUserToBackend(userDto)

                syncResult.onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }.onFailure { e ->
                    // Si el backend falla, informamos al usuario. 
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Error al sincronizar datos: ${e.message}"
                        ) 
                    }
                }

            }.onFailure { e ->
                val errorMessage = when {
                    e.message?.contains("email") == true &&
                            e.message?.contains("already") == true -> "Este correo ya está registrado"
                    e.message?.contains("network") == true -> "Error de conexión. Verifica tu internet"
                    e.message?.contains("badly formatted") == true -> "El formato del correo no es válido"
                    else -> "Error en Firebase: ${e.message}"
                }
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }
}