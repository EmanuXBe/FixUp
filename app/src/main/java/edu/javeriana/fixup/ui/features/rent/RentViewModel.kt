package edu.javeriana.fixup.ui.features.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import edu.javeriana.fixup.ui.model.PropertyModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

/**
 * ViewModel de la pantalla de listado de inmuebles (RentScreen).
 *
 * Responsabilidades:
 *  - Cargar y exponer la lista de propiedades.
 *  - Mantener el criterio de ordenamiento ([RentSort]) y la ubicación del dispositivo.
 *  - Re-ordenar la lista al cambiar el filtro o al recibir una nueva ubicación.
 *
 * La lógica de CREACIÓN de inmuebles vive en [CreatePropertyViewModel] (SRP).
 */
@HiltViewModel
class RentViewModel @Inject constructor(
    private val repository: RentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RentUiState>(RentUiState.Loading)
    val uiState: StateFlow<RentUiState> = _uiState.asStateFlow()

    /** Lista cruda (sin ordenar) para no perder el orden original al cambiar filtros. */
    private var rawProperties: List<PropertyModel> = emptyList()

    init {
        observeProperties()
    }

    /**
     * Stream en tiempo real desde Firestore: al publicar un inmueble nuevo (o borrar/editar)
     * la lista se actualiza sola, sin necesidad de pull-to-refresh ni de un callback de
     * navegación cuando el usuario vuelve de CreatePropertyScreen.
     */
    private fun observeProperties() {
        viewModelScope.launch {
            if (_uiState.value !is RentUiState.Success) {
                _uiState.value = RentUiState.Loading
            }
            repository.observeProperties()
                .catch { error ->
                    _uiState.value = RentUiState.Error("Error al cargar las propiedades: ${error.message}")
                }
                .collect { properties ->
                    rawProperties = properties
                    val current = _uiState.value as? RentUiState.Success
                    _uiState.value = RentUiState.Success(
                        properties = sortProperties(properties, current?.sort ?: RentSort.NONE, current?.userLocation),
                        sort = current?.sort ?: RentSort.NONE,
                        userLocation = current?.userLocation
                    )
                }
        }
    }

    /** Cambia el criterio de ordenamiento. Si es NEARBY pero no hay GPS, marca el flag. */
    fun setSort(sort: RentSort) {
        val current = _uiState.value as? RentUiState.Success ?: return
        val needsPerm = sort == RentSort.NEARBY && current.userLocation == null
        _uiState.value = current.copy(
            sort = sort,
            properties = sortProperties(rawProperties, sort, current.userLocation),
            needsLocationPermission = needsPerm
        )
    }

    /** Llamado desde la Screen cuando el FusedLocationProviderClient devuelve un fix. */
    fun setUserLocation(latitude: Double, longitude: Double) {
        val current = _uiState.value as? RentUiState.Success ?: return
        val loc = latitude to longitude
        _uiState.value = current.copy(
            userLocation = loc,
            properties = sortProperties(rawProperties, current.sort, loc),
            needsLocationPermission = false
        )
    }

    /** Llamado desde la Screen si el usuario rechazó el permiso de ubicación. */
    fun onLocationPermissionDenied() {
        val current = _uiState.value as? RentUiState.Success ?: return
        _uiState.value = current.copy(
            sort = RentSort.NONE,
            properties = sortProperties(rawProperties, RentSort.NONE, current.userLocation),
            needsLocationPermission = false
        )
    }

    private fun sortProperties(
        items: List<PropertyModel>,
        sort: RentSort,
        userLocation: Pair<Double, Double>?
    ): List<PropertyModel> = when (sort) {
        RentSort.NONE -> items
        RentSort.PRICE_ASC -> items.sortedBy { it.price ?: Double.MAX_VALUE }
        RentSort.PRICE_DESC -> items.sortedByDescending { it.price ?: Double.MIN_VALUE }
        RentSort.DATE_DESC -> items.sortedByDescending { it.createdAt ?: 0L }
        RentSort.NEARBY -> {
            if (userLocation == null) items
            else items.sortedBy { property ->
                val pLat = property.latitude
                val pLng = property.longitude
                if (pLat == null || pLng == null) Double.MAX_VALUE
                else haversineKm(userLocation.first, userLocation.second, pLat, pLng)
            }
        }
    }

    /** Distancia esférica (km) entre dos coordenadas — fórmula de Haversine. */
    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2.0)
        return 2 * r * asin(sqrt(a))
    }
}
