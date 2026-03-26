package edu.javeriana.fixup.ui.features.property_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val repository: RentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PropertyDetailUiState())
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    fun loadProperty(propertyId: String?) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = repository.getProperties()
            
            result.onSuccess { properties ->
                val property = properties.find { it.id == propertyId }
                _uiState.update { 
                    it.copy(
                        property = property,
                        isLoading = false,
                        error = if (property == null) "Propiedad no encontrada" else null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar la propiedad: ${error.message}"
                    )
                }
            }
        }
    }
}
