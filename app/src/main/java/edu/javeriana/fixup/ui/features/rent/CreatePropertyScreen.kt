package edu.javeriana.fixup.ui.features.rent

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Pantalla para publicar un nuevo inmueble.
 *
 * Arquitectura (Patrón Route/Screen — NowInAndroid):
 *   CreatePropertyRoute → observa ViewModel, maneja efectos de navegación
 *   CreatePropertyScreen → UI pura, recibe estado y callbacks (sin ViewModel directo)
 *
 * El patrón Route/Screen permite testear CreatePropertyScreen sin ViewModel:
 * se le pasa el estado y los callbacks manualmente en los tests.
 *
 * @param onBackClick  Callback para volver a la pantalla anterior
 * @param onSuccess    Callback invocado tras publicar exitosamente (navega atrás)
 * @param viewModel    Inyectado por Hilt; provee estado y acciones
 */
@Composable
fun CreatePropertyRoute(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreatePropertyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // LaunchedEffect: cuando success = true, navegar y limpiar el flag.
    // Usar LaunchedEffect(success) en lugar de if(success) en el body evita
    // que la navegación se llame múltiples veces en recomposiciones.
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.clearSuccess()
            onSuccess()
        }
    }

    CreatePropertyScreen(
        uiState            = uiState,
        onBackClick        = onBackClick,
        onSubmit           = { titulo, ubicacion, descripcion, precio, tipo, uris ->
            viewModel.submitProperty(titulo, ubicacion, descripcion, precio, tipo, uris)
        },
        onLocationSelected = viewModel::onLocationSelected,
        onClearError       = viewModel::clearError
    )
}

// ─── Alias de compatibilidad para AppNavigation.kt (no requiere cambios en el nav graph) ───
// AppNavigation llama a CreatePropertyScreen con onBackClick, onSuccess y viewModel.
// Redirigimos esos parámetros al Route para no tocar el nav graph.
@Composable
fun CreatePropertyScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreatePropertyViewModel = hiltViewModel()
) {
    CreatePropertyRoute(
        onBackClick = onBackClick,
        onSuccess   = onSuccess,
        viewModel   = viewModel
    )
}

// ─────────────────────────────────────────────────────────────────────────────────────────────
// Pantalla de UI pura (sin ViewModel)
// ─────────────────────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePropertyScreen(
    uiState: CreatePropertyUiState,
    onBackClick: () -> Unit,
    onSubmit: (titulo: String, ubicacion: String, descripcion: String,
               precio: String, tipo: String, imageUris: List<Uri>) -> Unit,
    onLocationSelected: (LatLng) -> Unit,
    onClearError: () -> Unit
) {
    // ─── Estado local del formulario ─────────────────────────────────────────
    // Los valores de los campos viven en el Composable (estado local de UI).
    // Solo cuando el usuario pulsa "Publicar" se envían al ViewModel.
    // Esto evita llamar al ViewModel en cada keystroke y mantiene la lógica
    // de negocio separada del estado de entrada del usuario.
    var titulo      by remember { mutableStateOf("") }
    var ubicacion   by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio      by remember { mutableStateOf("") }
    var tipo        by remember { mutableStateOf("") }  // "" | "Arriendo" | "Venta"

    // Lista de URIs de imágenes seleccionadas (estado local de UI)
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Launcher para seleccionar múltiples imágenes del almacenamiento del dispositivo.
    // GetMultipleContents devuelve una List<Uri> con todas las seleccionadas.
    // Las URIs nuevas se agregan a las ya existentes (sin reemplazar).
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            imageUris = (imageUris + uris).distinct() // evitar duplicados
        }
    }

    // SnackbarHostState para mostrar errores de servidor/red
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar el error general en un Snackbar cuando aparece
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(
                message     = errorMsg,
                duration    = SnackbarDuration.Long,
                actionLabel = "OK"
            )
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Publicar Inmueble",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick  = onBackClick,
                        enabled  = !uiState.isLoading  // bloquear durante carga
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ─── Sección 1: Información básica ────────────────────────────────
            FormSection(title = "Información del inmueble") {

                // Campo: Título
                PropertyTextField(
                    value         = titulo,
                    onValueChange = { titulo = it },
                    label         = "Título",
                    placeholder   = "Ej: Apartamento en Chapinero",
                    leadingIcon   = Icons.Default.Home,
                    errorMessage  = uiState.tituloError,
                    enabled       = !uiState.isLoading
                )

                // Campo: Ubicación
                PropertyTextField(
                    value         = ubicacion,
                    onValueChange = { ubicacion = it },
                    label         = "Ubicación",
                    placeholder   = "Ej: Chapinero Alto, Bogotá",
                    leadingIcon   = Icons.Default.LocationOn,
                    errorMessage  = uiState.ubicacionError,
                    enabled       = !uiState.isLoading
                )

                // Campo: Descripción (multiline)
                PropertyTextField(
                    value         = descripcion,
                    onValueChange = { descripcion = it },
                    label         = "Descripción",
                    placeholder   = "Describe el inmueble: habitaciones, baños, características...",
                    leadingIcon   = Icons.Default.Info,
                    errorMessage  = uiState.descripcionError,
                    minLines      = 3,
                    maxLines      = 6,
                    enabled       = !uiState.isLoading
                )

                // Campo: Precio
                PropertyTextField(
                    value         = precio,
                    onValueChange = { precio = it },
                    label         = "Precio (COP)",
                    placeholder   = "Ej: 2500000",
                    leadingIcon   = Icons.Default.AttachMoney,
                    errorMessage  = uiState.precioError,
                    keyboardType  = KeyboardType.Number,
                    enabled       = !uiState.isLoading
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ─── Sección 2: Tipo de negocio ───────────────────────────────────
            FormSection(title = "Tipo de negocio") {

                // Selector visual con dos chips: "Arriendo" y "Venta"
                // FilterChip da feedback visual claro de qué opción está activa
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TipoChip(
                        label     = "Arriendo",
                        selected  = tipo == "Arriendo",
                        onClick   = { if (!uiState.isLoading) tipo = "Arriendo" },
                        modifier  = Modifier.weight(1f)
                    )
                    TipoChip(
                        label    = "Venta",
                        selected  = tipo == "Venta",
                        onClick   = { if (!uiState.isLoading) tipo = "Venta" },
                        modifier  = Modifier.weight(1f)
                    )
                }

                // Error del campo tipo (si el usuario intenta publicar sin seleccionar)
                AnimatedVisibility(
                    visible = uiState.tipoError != null,
                    enter   = fadeIn(),
                    exit    = fadeOut()
                ) {
                    Text(
                        text  = uiState.tipoError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ─── Sección 3: Ubicación en el mapa ─────────────────────────────
            // Las coordenadas se persisten en el UiState (selectedLocation),
            // por lo que sobreviven a recomposiciones y rotaciones.
            FormSection(title = "Ubicación en el mapa") {
                Text(
                    text     = "Toca el mapa para marcar la ubicación exacta del inmueble.",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                LocationPickerMap(
                    selectedLocation   = uiState.selectedLocation,
                    onLocationSelected = onLocationSelected,
                    enabled            = !uiState.isLoading
                )
                AnimatedVisibility(
                    visible = uiState.ubicacionMapaError != null,
                    enter   = fadeIn(),
                    exit    = fadeOut()
                ) {
                    Text(
                        text  = uiState.ubicacionMapaError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                uiState.selectedLocation?.let { loc ->
                    Text(
                        text     = "Lat: %.5f  Lng: %.5f".format(loc.latitude, loc.longitude),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ─── Sección 4: Fotos del inmueble ───────────────────────────────
            FormSection(title = "Fotos del inmueble") {

                // Botón para abrir el selector de imágenes del sistema
                OutlinedButton(
                    onClick  = { imagePicker.launch("image/*") },
                    enabled  = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier           = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (imageUris.isEmpty()) "Agregar Fotos" else "Agregar Más Fotos"
                    )
                }

                // Error de imágenes (si el usuario intenta publicar sin fotos)
                AnimatedVisibility(
                    visible = uiState.imagenesError != null,
                    enter   = fadeIn(),
                    exit    = fadeOut()
                ) {
                    Text(
                        text  = uiState.imagenesError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Grid de previsualizaciones de imágenes seleccionadas.
                // Cada imagen muestra un botón "X" para eliminarla individualmente.
                if (imageUris.isNotEmpty()) {
                    ImagePreviewGrid(
                        imageUris = imageUris,
                        onRemove  = { uri ->
                            imageUris = imageUris.filter { it != uri }
                        },
                        enabled   = !uiState.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Botón de envío ───────────────────────────────────────────────
            Button(
                onClick = {
                    onSubmit(titulo, ubicacion, descripcion, precio, tipo, imageUris)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape   = RoundedCornerShape(14.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    // Durante la carga: mostrar spinner + texto de progreso
                    CircularProgressIndicator(
                        modifier  = Modifier.size(22.dp),
                        color     = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text  = "Publicando...",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Default.Publish,
                        contentDescription = null,
                        modifier           = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text       = "Publicar Inmueble",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Espacio al final para evitar que el botón quede pegado al borde inferior
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Centro inicial del mapa cuando el usuario aún no ha seleccionado ubicación.
// Bogotá es un punto razonable para la mayoría de los inmuebles de la app.
private val BOGOTA_DEFAULT = LatLng(4.711, -74.0721)

// ─────────────────────────────────────────────────────────────────────────────────────────────
// Componentes reutilizables privados de esta pantalla
// ─────────────────────────────────────────────────────────────────────────────────────────────

/**
 * Mapa interactivo que permite al usuario marcar la ubicación del inmueble.
 *
 * - onMapClick: captura el LatLng del toque y lo eleva al ViewModel para que
 *   persista en el UiState (sobrevive a recomposiciones y rotaciones).
 * - Marker: confirmación visual; solo se dibuja cuando hay coordenada elegida.
 *
 * ¿Por qué la cámara depende de selectedLocation con un key explícito?
 * Para que, si el ViewModel ya tenía una coordenada (vuelta a la pantalla,
 * cambio de configuración), la cámara abra centrada sobre el marker existente
 * en vez de en Bogotá por defecto.
 */
@Composable
private fun LocationPickerMap(
    selectedLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit,
    enabled: Boolean
) {
    val initialPosition = selectedLocation ?: BOGOTA_DEFAULT
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 14f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        GoogleMap(
            modifier            = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties          = MapProperties(isMyLocationEnabled = false),
            uiSettings          = MapUiSettings(
                zoomControlsEnabled  = false,
                myLocationButtonEnabled = false
            ),
            onMapClick = { latLng ->
                if (enabled) onLocationSelected(latLng)
            }
        ) {
            // Marker dibujado únicamente cuando ya hay una coordenada elegida.
            // MarkerState(position = ...) basta porque el LatLng proviene del
            // UiState, que es la fuente de verdad — no necesitamos rememberSaveable.
            selectedLocation?.let { loc ->
                Marker(
                    state = MarkerState(position = loc),
                    title = "Ubicación seleccionada"
                )
            }
        }
    }
}

/**
 * Contenedor con título de sección y slots para los campos.
 * Agrupa visualmente los campos relacionados con un encabezado consistente.
 */
@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

/**
 * OutlinedTextField con ícono, etiqueta, placeholder y soporte de error inline.
 * Centraliza el estilo de todos los campos del formulario.
 *
 * ¿Por qué el errorMessage viene del UiState y no como estado local?
 * Los errores de validación son lógica de negocio (qué es un título válido)
 * y deben vivir en el ViewModel, no en el Composable.
 */
@Composable
private fun PropertyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        placeholder   = { Text(placeholder, color = MaterialTheme.colorScheme.outline) },
        leadingIcon   = {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                tint               = if (errorMessage != null)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        },
        isError         = errorMessage != null,
        supportingText  = errorMessage?.let { msg ->
            { Text(text = msg, color = MaterialTheme.colorScheme.error) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines        = minLines,
        maxLines        = maxLines,
        enabled         = enabled,
        shape           = RoundedCornerShape(12.dp),
        modifier        = modifier.fillMaxWidth()
    )
}

/**
 * Chip de selección para el tipo de negocio (Arriendo / Venta).
 *
 * Usa FilterChip de Material Design 3: muestra visualmente cuál está seleccionado
 * con un fondo de color y un ícono de check. Más intuitivo que RadioButtons para
 * selecciones binarias en formularios móviles.
 */
@Composable
private fun TipoChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = {
            Text(
                text       = label,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null,
        shape    = RoundedCornerShape(10.dp),
        modifier = modifier.height(48.dp),
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor    = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor        = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor  = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Grid de 3 columnas con previsualizaciones de las imágenes seleccionadas.
 *
 * Cada celda muestra:
 *   - La imagen cargada con Coil (AsyncImage, igual que en toda la app)
 *   - Un botón circular "X" en la esquina superior derecha para eliminarla
 *
 * ¿Por qué no LazyVerticalGrid?
 * LazyVerticalGrid no funciona correctamente dentro de un Column con
 * verticalScroll. Usamos un layout manual con filas calculadas para evitar
 * el error "Nesting scrollable in the same direction is not allowed".
 */
@Composable
private fun ImagePreviewGrid(
    imageUris: List<Uri>,
    onRemove: (Uri) -> Unit,
    enabled: Boolean
) {
    val columns = 3
    val rows = (imageUris.size + columns - 1) / columns  // ceil division

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(rows) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                repeat(columns) { colIndex ->
                    val itemIndex = rowIndex * columns + colIndex
                    if (itemIndex < imageUris.size) {
                        ImagePreviewCell(
                            uri      = imageUris[itemIndex],
                            onRemove = { onRemove(imageUris[itemIndex]) },
                            enabled  = enabled,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Celda vacía para mantener el alineado del grid
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Celda individual del grid de imágenes.
 * Imagen con esquinas redondeadas y botón de eliminación superpuesto.
 */
@Composable
private fun ImagePreviewCell(
    uri: Uri,
    onRemove: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        // Imagen cargada con Coil — mismo patrón que AsyncImage en el resto de la app
        AsyncImage(
            model             = uri,
            contentDescription = "Foto del inmueble",
            contentScale      = ContentScale.Crop,
            modifier          = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                )
        )

        // Botón "X" superpuesto en esquina superior derecha
        IconButton(
            onClick  = onRemove,
            enabled  = enabled,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Eliminar foto",
                tint               = MaterialTheme.colorScheme.onErrorContainer,
                modifier           = Modifier.size(14.dp)
            )
        }
    }
}
