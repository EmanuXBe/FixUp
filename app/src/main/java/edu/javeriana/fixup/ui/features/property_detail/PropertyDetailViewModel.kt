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
                val idAsInt = propertyId?.toIntOrNull()
                val property = properties.find { it.id == idAsInt }
                _uiState.update { 
                    it.copy(
                        property = property,
                        isLoading = false,
                        error = if (property == null) "Propiedad no encontrada" else null
                    )
                }
                
                // Si la propiedad existe, cargamos sus reseñas
                idAsInt?.let { loadReviews(it) }
                
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

    private fun loadReviews(serviceId: Int) {
        viewModelScope.launch {
            repository.getReviewsByServiceId(serviceId).onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews) }
            }
        }
    }

    fun saveReview(serviceId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingReview = true) }
            repository.createReview(serviceId, rating, comment).onSuccess {
                _uiState.update { it.copy(isSavingReview = false) }
                loadReviews(serviceId) // Recargar reseñas después de guardar
            }.onFailure { error ->
                _uiState.update { it.copy(isSavingReview = false, error = error.message) }
            }
        }
    }
}
