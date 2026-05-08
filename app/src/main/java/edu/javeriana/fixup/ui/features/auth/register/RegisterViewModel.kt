package edu.javeriana.fixup.ui.features.auth.register

import android.util.Log
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

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onCedulaChanged(cedula: String) {
        _uiState.update { it.copy(cedula = cedula, error = null) }
    }

    fun onRoleSelected(role: String) {
        _uiState.update { it.copy(selectedRole = role, error = null) }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(error = null) }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.matches(emailRegex)
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

        // Validación robusta de campos
        if (email.isBlank() || password.isBlank() || cedula.isBlank() || role.isBlank()) {
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

        if (cedula.length < 5) {
            _uiState.update { it.copy(error = "La cédula debe ser válida") }
            return
        }

        viewModelScope.launch {
            // Iniciamos estado de carga global
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // PASO 1: Registro en Firebase
            val authResult = authRepository.signUp(
                email = email,
                password = password,
                cedula = cedula,
                role = role
            )
            
            authResult.onSuccess { firebaseUser ->
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()

                // PASO 2: Sincronización con el Backend (PostgreSQL) - NON-BLOCKING
                viewModelScope.launch {
                    try {
                        val userDto = UserDto(
                            id = firebaseUser.uid,
                            name = email.substringBefore("@"),
                            email = email,
                            phone = cedula,
                            address = null,
                            role = role,
                            profileImageUrl = null
                        )
                        authRepository.syncUserToBackend(userDto).onFailure { e ->
                            Log.e("RegisterViewModel", "Error en syncUserToBackend: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterViewModel", "Excepción en syncUserToBackend", e)
                    }
                }
            }.onFailure { e ->
                // El repositorio ya devuelve mensajes amigables vía AppError
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
