package edu.javeriana.fixup.ui.model

/**
 * Representa una propiedad inmobiliaria en alquiler.
 *
 * @property id Identificador único de la propiedad.
 * @property title Título descriptivo del anuncio.
 * @property description Descripción detallada de la propiedad.
 * @property price Precio mensual del alquiler en Pesos Colombianos (COP).
 * @property bedrooms Número de habitaciones.
 * @property bathrooms Número de baños.
 * @property hasParking Indica si cuenta con parqueadero privado.
 * @property isFeatured Indica si es una propiedad destacada.
 * @property isNew Indica si el inmueble es nuevo o para estrenar.
 * @property rating Calificación promedio otorgada por usuarios.
 * @property reviewCount Cantidad de reseñas recibidas.
 * @property distanceKm Distancia en kilómetros desde el centro o punto de interés.
 * @property imageUrl URL de la imagen principal de la propiedad.
 */
data class PropertyModel(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val hasParking: Boolean,
    val isFeatured: Boolean,
    val isNew: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val imageUrl: String
)
