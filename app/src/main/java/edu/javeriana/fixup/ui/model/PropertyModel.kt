package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una propiedad inmobiliaria o servicio.
 * Actualizado para coincidir con el contrato profesional del backend.
 */
data class PropertyModel(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)
