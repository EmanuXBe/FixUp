package edu.javeriana.fixup.ui.features.property_detail

import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel

data class PropertyDetailUiState(
    val property: PropertyModel? = null,
    val reviews: List<ReviewModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSavingReview: Boolean = false,
    val error: String? = null
)
