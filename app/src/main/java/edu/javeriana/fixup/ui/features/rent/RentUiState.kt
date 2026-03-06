package edu.javeriana.fixup.ui.features.rent

import edu.javeriana.fixup.ui.model.PropertyModel

sealed interface RentUiState {
    data object Loading : RentUiState
    data class Success(val properties: List<PropertyModel>) : RentUiState
    data class Error(val message: String) : RentUiState
}
