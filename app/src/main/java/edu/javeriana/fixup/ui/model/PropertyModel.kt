package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una propiedad inmobiliaria o servicio.
 * Actualizado para reflejar los campos de la nueva API REST.
 */
data class PropertyModel(
    val id: String? = null,
    val title: String,
    val description: String,
    val price: Double,
    val location: String,
    @SerializedName("image_url")
    val imageUrl: String? = null
)
