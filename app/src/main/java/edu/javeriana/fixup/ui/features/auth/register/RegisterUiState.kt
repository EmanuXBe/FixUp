package edu.javeriana.fixup.ui.features.auth.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val cedula: String = "",
    val selectedRole: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
