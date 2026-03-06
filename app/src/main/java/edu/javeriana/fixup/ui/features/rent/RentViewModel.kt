package edu.javeriana.fixup.ui.features.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.ui.model.MockPropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            try {
                val properties = MockPropertyRepository.getProperties()
                _uiState.value = RentUiState.Success(properties)
            } catch (e: Exception) {
                _uiState.value = RentUiState.Error("Ocurrió un error al cargar las propiedades: ${e.message}")
            }
        }
    }

    fun onPropertySelected(propertyId: String) {
        // Lógica adicional si es necesaria
    }
}
