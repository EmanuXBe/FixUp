package edu.javeriana.fixup.ui.features.map

data class PropertyMapMarker(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double?,
    val imageUrl: String? = null
)

data class MapUiState(
    val isLoading: Boolean = true,
    val markers: List<PropertyMapMarker> = emptyList(),
    val error: String? = null
)
