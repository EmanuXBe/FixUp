package edu.javeriana.fixup.ui.features.review_map

import edu.javeriana.fixup.ui.model.ReviewMapModel

data class ReviewMapUiState(
    val isLoading: Boolean = true,
    val reviews: List<ReviewMapModel> = emptyList(),
    val selectedReview: ReviewMapModel? = null,
    val error: String? = null
)
