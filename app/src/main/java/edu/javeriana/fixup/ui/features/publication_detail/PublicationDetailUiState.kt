package edu.javeriana.fixup.ui.features.publication_detail

import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.ReviewModel

data class PublicationDetailUiState(
    val publication: PublicationCardModel? = null,
    val description: String = "",
    val reviews: List<ReviewModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSendingReview: Boolean = false,
    val reviewSent: Boolean = false,
    val reviewError: String? = null,
    val error: String? = null
)
