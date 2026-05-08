package edu.javeriana.fixup.ui.features.rent

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
    val imagenesError: String? = null
) {
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
                imagenesError == null
}
