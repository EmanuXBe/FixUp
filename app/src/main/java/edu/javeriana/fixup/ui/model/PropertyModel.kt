package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de dominio para un inmueble.
 *
 * El campo [id] es String para soportar:
 *   - IDs de Firestore: strings alfanuméricos autogenerados (ej: "Kx3pQzAb...")
 *   - IDs de mocks locales: strings numéricos ("101", "102", ...) para backwards-compat
 */
data class PropertyModel(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    /** Epoch millis. Necesario para ordenar por fecha de publicación en RentScreen. */
    @SerializedName("created_at")
    val createdAt: Long? = null
)
