package edu.javeriana.fixup.data.network.dto

import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName

/** DTO legado — mantiene compatibilidad con endpoints que usan nombres en inglés. */
data class PropertyDto(
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

/**
 * DTO de respuesta para GET /api/properties (datos guardados en Firestore).
 * Los campos usan nombres en español, tal como los persiste el backend Express.
 */
data class FirestorePropertyDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("titulo")
    val titulo: String? = null,
    @SerializedName("ubicacion")
    val ubicacion: String? = null,
    @SerializedName("descripcion")
    val descripcion: String? = null,
    @SerializedName("precio")
    val precio: Double? = null,
    @SerializedName("tipo")
    val tipo: String? = null,
    @SerializedName("imagenes")
    val imagenes: List<String>? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    // GeoPoint nativo de Firestore — no usa @SerializedName porque Gson no soporta este tipo.
    // Se lee con DocumentSnapshot.getGeoPoint("location") en la capa de datos.
    val location: GeoPoint? = null
)
