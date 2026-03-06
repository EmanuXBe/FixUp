package edu.javeriana.fixup.ui.features.property_detail

import edu.javeriana.fixup.ui.model.PropertyModel

data class PropertyDetailUiState(
    val property: PropertyModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
