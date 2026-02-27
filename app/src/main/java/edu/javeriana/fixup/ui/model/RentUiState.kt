package edu.javeriana.fixup.ui.model

/**
 * Representa los diferentes estados de la interfaz de usuario para la pantalla de alquileres.
 */
sealed interface RentUiState {
    /**
     * Estado inicial de carga mientras se obtienen los datos.
     */
    data object Loading : RentUiState

    /**
     * Estado de éxito cuando se han obtenido las propiedades correctamente.
     * @property properties Lista de propiedades inmobiliarias.
     */
    data class Success(val properties: List<PropertyModel>) : RentUiState

    /**
     * Estado de error cuando ocurre un fallo al cargar los datos.
     * @property message Mensaje descriptivo del error.
     */
    data class Error(val message: String) : RentUiState
}
