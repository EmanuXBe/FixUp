package edu.javeriana.fixup.ui.features.auth.login

data class LogInUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
