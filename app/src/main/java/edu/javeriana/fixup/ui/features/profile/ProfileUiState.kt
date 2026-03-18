package edu.javeriana.fixup.ui.features.profile

data class ProfileUiState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
