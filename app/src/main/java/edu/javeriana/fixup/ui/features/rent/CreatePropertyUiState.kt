package edu.javeriana.fixup.ui.features.rent

import com.google.android.gms.maps.model.LatLng

/**
 * Estado de UI para el formulario de publicación de inmuebles (CreatePropertyScreen).
 *
 * Se usa un data class (no sealed interface) porque el formulario tiene múltiples
 * propiedades que coexisten simultáneamente: puede estar cargando Y tener errores
 * de campo al mismo tiempo, algo que un sealed interface no permite modelar bien.
 *
 * Patrón: cada campo del formulario tiene su propio campo de error independiente,
 * lo que permite mostrar el mensaje de error justo debajo del campo incorrecto
 * en lugar de un mensaje genérico al final del formulario.
 *
 * @param isLoading         true mientras se suben imágenes y se llama al backend
 * @param success           true cuando el inmueble fue publicado exitosamente
 * @param error             mensaje de error general (errores del servidor o de red)
 * @param tituloError       mensaje de error para el campo Título
 * @param ubicacionError    mensaje de error para el campo Ubicación
 * @param descripcionError  mensaje de error para el campo Descripción
 * @param precioError       mensaje de error para el campo Precio
 * @param tipoError         mensaje de error para el selector Tipo (Arriendo/Venta)
 * @param imagenesError     mensaje de error si no se seleccionó ninguna foto
 * @param selectedLocation  Coordenadas elegidas por el usuario en el mapa.
 *                          Se persisten en el ViewModel para sobrevivir a
 *                          recomposiciones y cambios de configuración.
 * @param ubicacionMapaError mensaje de error si el usuario no marcó un punto en el mapa
 */
data class CreatePropertyUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val tituloError: String? = null,
    val ubicacionError: String? = null,
    val descripcionError: String? = null,
    val precioError: String? = null,
    val tipoError: String? = null,
    val imagenesError: String? = null,
    val selectedLocation: LatLng? = null,
    val ubicacionMapaError: String? = null
) {
    /** Latitud seleccionada o null si el usuario no ha tocado el mapa. */
    val latitude: Double?
        get() = selectedLocation?.latitude

    /** Longitud seleccionada o null si el usuario no ha tocado el mapa. */
    val longitude: Double?
        get() = selectedLocation?.longitude

    /**
     * Propiedad computada: true si NO hay ningún error de validación activo.
     * Usada en la Screen para saber si el formulario es envíable.
     */
    val isFormValid: Boolean
        get() = tituloError == null &&
                ubicacionError == null &&
                descripcionError == null &&
                precioError == null &&
                tipoError == null &&
                imagenesError == null &&
                ubicacionMapaError == null
}
