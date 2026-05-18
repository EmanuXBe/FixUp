package edu.javeriana.fixup.ui.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val rentRepository: RentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        observeProperties()
    }

    /**
     * Se suscribe al stream en tiempo real de Firestore (`addSnapshotListener`).
     * Cualquier publicación nueva creada desde la app aparece automáticamente
     * en el mapa sin necesidad de pull-to-refresh ni de volver a abrir la pantalla.
     */
    private fun observeProperties() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            rentRepository.observeProperties()
                .catch {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudieron cargar las propiedades"
                    )
                }
                .collect { properties ->
                    val markers = properties.mapNotNull { property ->
                        val lat = property.latitude ?: return@mapNotNull null
                        val lng = property.longitude ?: return@mapNotNull null
                        val id = property.id ?: return@mapNotNull null
                        PropertyMapMarker(
                            id = id,
                            title = property.title ?: "Propiedad",
                            latitude = lat,
                            longitude = lng,
                            price = property.price,
                            imageUrl = property.imageUrl
                        )
                    }
                    _uiState.value = MapUiState(isLoading = false, markers = markers)
                }
        }
    }
}
