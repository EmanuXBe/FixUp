package edu.javeriana.fixup.ui.features.publication_detail

import edu.javeriana.fixup.ui.features.feed.PublicationCardModel

data class PublicationDetailUiState(
    val publication: PublicationCardModel? = null,
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
