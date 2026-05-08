package edu.javeriana.fixup.ui.features.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de listado de inmuebles (RentScreen).
 *
 * Responsabilidad única: cargar y exponer la lista de propiedades disponibles.
 * La lógica de CREACIÓN de inmuebles se delegó a [CreatePropertyViewModel]
 * para respetar el Principio de Responsabilidad Única (SRP).
 */
@HiltViewModel
class RentViewModel @Inject constructor(
    private val repository: RentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            // Evitar parpadeo: solo mostrar Loading en la primera carga
            if (_uiState.value !is RentUiState.Success) {
                _uiState.value = RentUiState.Loading
            }

            repository.getProperties()
                .onSuccess { properties ->
                    _uiState.value = RentUiState.Success(properties)
                }
                .onFailure { error ->
                    _uiState.value = RentUiState.Error(
                        "Error al cargar las propiedades: ${error.message}"
                    )
                }
        }
    }
}
