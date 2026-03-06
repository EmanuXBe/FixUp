package edu.javeriana.fixup.ui.features.profile

data class ProfileUiState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "",
    val isLoading: Boolean = false
)
