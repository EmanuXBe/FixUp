package edu.javeriana.fixup.ui.features.rent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    private val repository: RentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            _uiState.value = RentUiState.Loading
            val result = repository.getProperties()
            
            result.onSuccess { properties ->
                _uiState.value = RentUiState.Success(properties)
            }.onFailure { error ->
                _uiState.value = RentUiState.Error("Ocurrió un error al cargar las propiedades: ${error.message}")
            }
        }
    }

    fun createProperty(property: PropertyModel, imageUri: Uri, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = repository.createProperty(property, imageUri)
            result.onSuccess {
                loadProperties()
                onComplete()
            }.onFailure {
                // Manejar error de creación si es necesario
                onComplete()
            }
        }
    }

    fun onPropertySelected(propertyId: String) {
        // Lógica adicional si es necesaria
    }
}
