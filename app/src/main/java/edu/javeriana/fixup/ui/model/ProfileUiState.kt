package edu.javeriana.fixup.ui.model

data class ProfileUiState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "Cliente estrella",
    val isLoading: Boolean = false
)
