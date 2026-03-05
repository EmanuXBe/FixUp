package edu.javeriana.fixup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.ui.model.MockPropertyRepository
import edu.javeriana.fixup.ui.model.PropertyDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PropertyDetailUiState())
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    fun loadProperty(propertyId: String?) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val property = MockPropertyRepository.getProperties().find { it.id == propertyId }
            _uiState.update { 
                it.copy(
                    property = property,
                    isLoading = false,
                    error = if (property == null) "Propiedad no encontrada" else null
                )
            }
        }
    }
}
