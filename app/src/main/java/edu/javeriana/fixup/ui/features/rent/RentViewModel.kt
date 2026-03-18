package edu.javeriana.fixup.ui.features.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.data.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RentViewModel(
    private val repository: RentRepository = RentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            val result = repository.getProperties()
            
            result.onSuccess { properties ->
                _uiState.value = RentUiState.Success(properties)
            }.onFailure { error ->
                _uiState.value = RentUiState.Error("Ocurrió un error al cargar las propiedades: ${error.message}")
            }
        }
    }

    fun onPropertySelected(propertyId: String) {
        // Lógica adicional si es necesaria
    }
}
