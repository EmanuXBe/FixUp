package edu.javeriana.fixup.ui.model

data class PublicationUiState(
    val publication: PublicationCardModel? = null,
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
