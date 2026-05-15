package edu.javeriana.fixup.ui.features.rent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
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

    // StateFlow privado y mutable: solo el ViewModel puede modificarlo.
    // Incluye selectedLocation (LatLng?) para que las coordenadas elegidas
    // en el mapa sobrevivan a recomposiciones y cambios de configuración.
    private val _uiState = MutableStateFlow(CreatePropertyUiState())

    // StateFlow público e inmutable: la Screen solo puede observarlo
    val uiState: StateFlow<CreatePropertyUiState> = _uiState.asStateFlow()

    // ─── Acciones públicas ────────────────────────────────────────────────────

    /**
     * Llamado por la Screen cuando el usuario toca el mapa.
     *
     * Persiste las coordenadas en el UiState para que (a) el Marker visual se
     * mantenga al recomponer, y (b) el submit pueda asociarlas al inmueble.
     * También limpia cualquier error previo del campo de mapa.
     */
    fun onLocationSelected(latLng: LatLng) {
        _uiState.update {
            it.copy(
                selectedLocation   = latLng,
                ubicacionMapaError = null
            )
        }
    }

    /**
     * Intenta publicar el inmueble con los datos del formulario.
     *
     * FLUJO:
     *   1. Validar todos los campos (incluyendo selectedLocation del UiState)
     *   2. Cambiar isLoading = true para bloquear el botón y mostrar spinner
     *   3. Delegar al Repository (que a su vez llama al DataSource):
     *      a. DataSource sube cada imagen a Firebase Storage (await por corrutina)
     *      b. DataSource llama a POST /api/properties con las URLs y coordenadas
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
        // Coordenadas tomadas del estado: el mapa las depositó allí mediante
        // onLocationSelected. Si son null, validateFields rechazará el envío.
        val location = _uiState.value.selectedLocation

        // ─── PASO 1: Validación previa al envío ───────────────────────────────
        if (!validateFields(titulo, ubicacion, descripcion, precio, tipo, imageUris, location)) {
            return
        }

        val precioDouble = precio.toDouble()
        // En este punto location no puede ser null (validateFields lo garantiza)
        val safeLocation = location!!

        // ─── PASO 2: Iniciar la operación asíncrona ───────────────────────────
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // ─── PASO 3: Delegar al Repository ────────────────────────────────
            rentRepository.createProperty(
                titulo      = titulo.trim(),
                ubicacion   = ubicacion.trim(),
                descripcion = descripcion.trim(),
                precio      = precioDouble,
                tipo        = tipo,
                imageUris   = imageUris,
                latitude    = safeLocation.latitude,
                longitude   = safeLocation.longitude
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, success = true) }
            }.onFailure { error ->
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
     */
    private fun validateFields(
        titulo: String,
        ubicacion: String,
        descripcion: String,
        precio: String,
        tipo: String,
        imageUris: List<Uri>,
        location: LatLng?
    ): Boolean {
        val tituloErr      = if (titulo.isBlank()) "El título es obligatorio" else null
        val ubicacionErr   = if (ubicacion.isBlank()) "La ubicación es obligatoria" else null
        val descripcionErr = if (descripcion.isBlank()) "La descripción es obligatoria" else null

        val precioErr = when {
            precio.isBlank()                           -> "El precio es obligatorio"
            precio.toDoubleOrNull() == null            -> "Ingresa un número válido"
            (precio.toDoubleOrNull() ?: -1.0) < 0     -> "El precio no puede ser negativo"
            else                                       -> null
        }

        val tipoErr     = if (tipo.isBlank()) "Selecciona un tipo: Arriendo o Venta" else null
        val imagenesErr = if (imageUris.isEmpty()) "Agrega al menos una foto del inmueble" else null
        val mapaErr     = if (location == null) "Toca el mapa para marcar la ubicación exacta" else null

        _uiState.update {
            it.copy(
                tituloError        = tituloErr,
                ubicacionError     = ubicacionErr,
                descripcionError   = descripcionErr,
                precioError        = precioErr,
                tipoError          = tipoErr,
                imagenesError      = imagenesErr,
                ubicacionMapaError = mapaErr
            )
        }

        return tituloErr == null &&
                ubicacionErr == null &&
                descripcionErr == null &&
                precioErr == null &&
                tipoErr == null &&
                imagenesErr == null &&
                mapaErr == null
    }
}
