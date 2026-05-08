package edu.javeriana.fixup.ui.features.rent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel exclusivo para el formulario de publicación de inmuebles.
 *
 * ¿Por qué un ViewModel separado de RentViewModel?
 * RentViewModel maneja el LISTADO. Este ViewModel maneja la CREACIÓN.
 * Separar responsabilidades (SRP) evita que el ViewModel de listado
 * acumule lógica de validación de formularios, subida de imágenes
 * y manejo de estados de carga, que son concerns completamente distintos.
 *
 * Flujo de datos (Unidireccional — UDF):
 *   Screen → onSubmit() → ViewModel.submitProperty() → Repository
 *                                                          ↓
 *   Screen ← uiState (StateFlow) ←── _uiState.update { ... }
 *
 * @param rentRepository Repositorio que orquesta Storage + Backend Express
 */
@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val rentRepository: RentRepository
) : ViewModel() {

    // StateFlow privado y mutable: solo el ViewModel puede modificarlo
    private val _uiState = MutableStateFlow(CreatePropertyUiState())

    // StateFlow público e inmutable: la Screen solo puede observarlo
    val uiState: StateFlow<CreatePropertyUiState> = _uiState.asStateFlow()

    // ─── Acciones públicas ────────────────────────────────────────────────────

    /**
     * Intenta publicar el inmueble con los datos del formulario.
     *
     * FLUJO:
     *   1. Validar todos los campos → si hay errores, actualizar estado y abortar
     *   2. Cambiar isLoading = true para bloquear el botón y mostrar spinner
     *   3. Delegar al Repository (que a su vez llama al DataSource):
     *      a. DataSource sube cada imagen a Firebase Storage (await por corrutina)
     *      b. DataSource llama a POST /api/properties con las URLs obtenidas
     *   4. En éxito: success = true (la Screen navega de vuelta)
     *   5. En fallo: error = mensaje descriptivo, isLoading = false
     *
     * @param titulo      Texto del campo Título
     * @param ubicacion   Texto del campo Ubicación
     * @param descripcion Texto del campo Descripción
     * @param precio      Texto del campo Precio (se valida y convierte a Double aquí)
     * @param tipo        Valor seleccionado: "Arriendo" o "Venta" (o vacío si no seleccionó)
     * @param imageUris   Lista de URIs locales de las fotos seleccionadas por el usuario
     */
    fun submitProperty(
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: String,
        tipo: String,
        imageUris: List<Uri>
    ) {
        // ─── PASO 1: Validación previa al envío ───────────────────────────────
        //
        // ¿Por qué validar en el ViewModel y no en la Screen?
        // La Screen solo muestra los errores; la LÓGICA de qué es válido
        // pertenece al ViewModel. Así la validación es testeable y no
        // depende de la existencia de los Composables.
        if (!validateFields(titulo, ubicacion, descripcion, precio, tipo, imageUris)) {
            return // validateFields ya actualizó _uiState con los errores
        }

        // Convertir precio a Double (la validación garantizó que es un número)
        val precioDouble = precio.toDouble()

        // ─── PASO 2: Iniciar la operación asíncrona ───────────────────────────
        viewModelScope.launch {
            // Activar estado de carga: bloquea el botón y muestra CircularProgressIndicator
            _uiState.update { it.copy(isLoading = true, error = null) }

            // ─── PASO 3: Delegar al Repository ────────────────────────────────
            //
            // El Repository obtiene el userId del AuthRepository de forma interna,
            // manteniendo el ViewModel libre de depender directamente de Firebase Auth.
            // viewModelScope garantiza que la corrutina se cancela si el usuario
            // abandona la pantalla, evitando memory leaks.
            rentRepository.createProperty(
                titulo      = titulo.trim(),
                ubicacion   = ubicacion.trim(),
                descripcion = descripcion.trim(),
                precio      = precioDouble,
                tipo        = tipo,
                imageUris   = imageUris
            ).onSuccess {
                // ─── PASO 4: Éxito ────────────────────────────────────────────
                // success = true dispara el LaunchedEffect en la Screen que navega atrás
                _uiState.update { it.copy(isLoading = false, success = true) }

            }.onFailure { error ->
                // ─── PASO 5: Error ────────────────────────────────────────────
                // Mostramos el mensaje del error al usuario (red caída, Storage falla, etc.)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Ocurrió un error al publicar el inmueble."
                    )
                }
            }
        }
    }

    /**
     * Limpia el mensaje de error general (llamado desde la Screen al descartar el Snackbar).
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Limpia el flag de éxito (llamado desde la Screen después de navegar).
     * Previene que el LaunchedEffect se dispare de nuevo si el ViewModel sobrevive.
     */
    fun clearSuccess() {
        _uiState.update { it.copy(success = false) }
    }

    // ─── Lógica privada ───────────────────────────────────────────────────────

    /**
     * Valida todos los campos del formulario y actualiza _uiState con los errores.
     *
     * @return true si todos los campos son válidos, false si hay al menos uno inválido.
     *
     * ¿Por qué validar todos los campos de una vez (no hacer return en el primero)?
     * Si retornáramos al primer error, el usuario solo vería un campo rojo a la vez.
     * Validando todo de golpe mostramos todos los campos con error simultáneamente,
     * dando una mejor experiencia: el usuario puede corregirlos todos antes de volver a enviar.
     */
    private fun validateFields(
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: String,
        tipo: String,
        imageUris: List<Uri>
    ): Boolean {
        val tituloErr      = if (titulo.isBlank()) "El título es obligatorio" else null
        val ubicacionErr   = if (ubicacion.isBlank()) "La ubicación es obligatoria" else null
        val descripcionErr = if (descripcion.isBlank()) "La descripción es obligatoria" else null

        // El precio debe ser un número positivo
        val precioErr = when {
            precio.isBlank()                           -> "El precio es obligatorio"
            precio.toDoubleOrNull() == null            -> "Ingresa un número válido"
            (precio.toDoubleOrNull() ?: -1.0) < 0     -> "El precio no puede ser negativo"
            else                                       -> null
        }

        val tipoErr     = if (tipo.isBlank()) "Selecciona un tipo: Arriendo o Venta" else null
        val imagenesErr = if (imageUris.isEmpty()) "Agrega al menos una foto del inmueble" else null

        // Actualizar el estado con todos los errores calculados
        _uiState.update {
            it.copy(
                tituloError      = tituloErr,
                ubicacionError   = ubicacionErr,
                descripcionError = descripcionErr,
                precioError      = precioErr,
                tipoError        = tipoErr,
                imagenesError    = imagenesErr
            )
        }

        // El formulario es válido solo si todos los errores son null
        return tituloErr == null &&
                ubicacionErr == null &&
                descripcionErr == null &&
                precioErr == null &&
                tipoErr == null &&
                imagenesErr == null
    }
}
