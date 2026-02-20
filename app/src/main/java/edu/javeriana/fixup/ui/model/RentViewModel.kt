package edu.javeriana.fixup.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de negocio para la pantalla de alquileres.
 * Utiliza el patrón de arquitectura recomendada por Android (MVI/MVVM).
 */
class RentViewModel : ViewModel() {

    // Estado interno MutableStateFlow
    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)

    // Estado expuesto públicamente como StateFlow inmutable
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    /**
     * Carga las propiedades desde el repositorio ficticio y actualiza el estado.
     */
    private fun loadProperties() {
        viewModelScope.launch {
            try {
                // Simulación de carga (podría incluir un delay de red)
                val properties = MockPropertyRepository.getProperties()
                _uiState.value = RentUiState.Success(properties)
            } catch (e: Exception) {
                _uiState.value = RentUiState.Error("Ocurrió un error al cargar las propiedades: ${e.message}")
            }
        }
    }

    /**
     * Se ejecuta cuando el usuario hace clic en un filtro de la lista.
     * @param filterType El tipo de filtro seleccionado.
     */
    fun onFilterClicked(filterType: String) {
        // TODO: Implementar lógica de filtrado
    }

    /**
     * Se ejecuta cuando el usuario selecciona una propiedad específica.
     * @param propertyId Identificador único de la propiedad.
     */
    fun onPropertySelected(propertyId: String) {
        // TODO: Implementar lógica de navegación al detalle
    }
}
