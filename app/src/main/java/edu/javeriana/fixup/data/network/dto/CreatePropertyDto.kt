package edu.javeriana.fixup.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO de REQUEST para POST /api/properties.
 *
 * Los nombres de campo coinciden exactamente con el contrato del backend Express
 * (propertyController.js). Los @SerializedName aseguran que Gson serialice los
 * campos con los nombres en español que espera el servidor, independientemente
 * de cómo se llamen las propiedades en Kotlin.
 *
 * ¿Por qué un DTO separado de PropertyDto?
 * PropertyDto existe para deserializar la RESPUESTA del GET /api/properties
 * (datos en inglés del mock). CreatePropertyRequestDto refleja el contrato
 * del POST con campos en español. Mezclarlos en una sola clase generaría
 * confusión y no respetaría el principio de responsabilidad única.
 */
data class CreatePropertyRequestDto(
    /** Firebase UID del usuario que publica el inmueble. */
    @SerializedName("userId")
    val userId: String,

    /** Título descriptivo del inmueble (ej: "Apartamento en Chapinero"). */
    @SerializedName("titulo")
    val titulo: String,

    /** Dirección o zona geográfica del inmueble. */
    @SerializedName("ubicacion")
    val ubicacion: String,

    /** Descripción detallada del inmueble. */
    @SerializedName("descripcion")
    val descripcion: String,

    /** Precio en pesos colombianos (COP). */
    @SerializedName("precio")
    val precio: Double,

    /** Modalidad del negocio. Solo "Arriendo" o "Venta" son valores válidos. */
    @SerializedName("tipo")
    val tipo: String,

    /**
     * URLs de las fotos ya subidas a Firebase Storage.
     * El cliente sube las imágenes directamente a Storage (patrón client-side upload)
     * y envía aquí solo las URLs resultantes, nunca los binarios.
     */
    @SerializedName("imagenes")
    val imagenes: List<String> = emptyList(),

    /** Latitud seleccionada por el usuario en el mapa. */
    @SerializedName("latitude")
    val latitude: Double? = null,

    /** Longitud seleccionada por el usuario en el mapa. */
    @SerializedName("longitude")
    val longitude: Double? = null
)

/**
 * DTO de RESPUESTA del backend al crear un inmueble exitosamente (HTTP 201).
 *
 * El backend devuelve:
 * { "message": "...", "propertyId": "abc123", "property": { ... } }
 *
 * Solo mapeamos los campos que la app necesita: el ID generado por Firestore.
 */
data class CreatePropertyResponseDto(
    /** ID autogenerado por Firestore para el nuevo documento. */
    @SerializedName("propertyId")
    val propertyId: String?,

    /** Mensaje de confirmación del servidor. */
    @SerializedName("message")
    val message: String?
)
