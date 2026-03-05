package edu.javeriana.fixup.ui.model

data class PropertyDetailUiState(
    val property: PropertyModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
