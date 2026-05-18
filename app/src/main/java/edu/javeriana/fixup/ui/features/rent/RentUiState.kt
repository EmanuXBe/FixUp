package edu.javeriana.fixup.ui.features.rent

import edu.javeriana.fixup.ui.model.PropertyModel

/**
 * Criterio de ordenamiento aplicado al listado de inmuebles en RentScreen.
 * - [NONE]: orden original tal como vino del data source.
 * - [PRICE_ASC] / [PRICE_DESC]: por precio.
 * - [DATE_DESC]: más recientes primero.
 * - [NEARBY]: por cercanía al GPS del dispositivo (requiere permiso de ubicación).
 */
enum class RentSort { NONE, PRICE_ASC, PRICE_DESC, DATE_DESC, NEARBY }

sealed interface RentUiState {
    data object Loading : RentUiState
    data class Success(
        val properties: List<PropertyModel>,
        val sort: RentSort = RentSort.NONE,
        /** Ubicación actual del dispositivo (lat, lng). Solo se usa cuando sort=NEARBY. */
        val userLocation: Pair<Double, Double>? = null,
        /** Indica que se seleccionó NEARBY pero aún no se concedió permiso o no hay fix. */
        val needsLocationPermission: Boolean = false
    ) : RentUiState
    data class Error(val message: String) : RentUiState
}
