package edu.javeriana.fixup.ui.features.map

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CoffeeColor = android.graphics.Color.parseColor("#CEAC78")
private const val MARKER_SIZE_PX = 120
private const val BORDER_PX = 10f

@Composable
private fun rememberPhotoMarker(imageUrl: String?): BitmapDescriptor? {
    val context = LocalContext.current
    var descriptor by remember(imageUrl) { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(imageUrl) {
        descriptor = withContext(Dispatchers.IO) {
            try {
                if (imageUrl.isNullOrBlank()) return@withContext null
                // Usar el singleton de Coil (extension `context.imageLoader`) en lugar de
                // construir un ImageLoader nuevo por marker. Cada ImageLoader tiene su
                // propio thread pool y cache de disco; crearlos repetidamente es costoso.
                val req = ImageRequest.Builder(context).data(imageUrl).allowHardware(false).build()
                val result = context.imageLoader.execute(req)
                if (result !is SuccessResult) return@withContext null
                val src = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                    ?: return@withContext null

                val size = MARKER_SIZE_PX
                val innerSize = size - (BORDER_PX * 2).toInt()

                val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(output)

                // Marco cuadrado (no circular) en color café
                val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = CoffeeColor
                    style = Paint.Style.FILL
                }
                canvas.drawRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), borderPaint)

                // Center-crop a cuadrado para conservar aspect ratio y evitar deformación
                val cropSide = minOf(src.width, src.height)
                val cropX = (src.width - cropSide) / 2
                val cropY = (src.height - cropSide) / 2
                val squareSrc = Bitmap.createBitmap(src, cropX, cropY, cropSide, cropSide)
                val scaled = Bitmap.createScaledBitmap(squareSrc, innerSize, innerSize, true)

                // Pintamos directamente el bitmap cuadrado dentro del marco (sin clip circular)
                canvas.drawBitmap(scaled, BORDER_PX, BORDER_PX, Paint(Paint.ANTI_ALIAS_FLAG))

                val descriptor = BitmapDescriptorFactory.fromBitmap(output)
                // Liberar bitmaps intermedios: fromBitmap() ya subió los pixeles a GPU,
                // así que mantener estas copias en heap solo presiona el GC con muchos markers.
                // No reciclamos `src` porque pertenece al cache de Coil.
                if (squareSrc !== src) squareSrc.recycle()
                scaled.recycle()
                output.recycle()
                descriptor
            } catch (_: Exception) {
                null
            }
        }
    }
    return descriptor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onMarkerClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bogota, 11f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de propiedades") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                uiState.markers.forEach { marker ->
                    PhotoMarker(
                        marker = marker,
                        onMarkerClick = onMarkerClick
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun PhotoMarker(
    marker: PropertyMapMarker,
    onMarkerClick: (String) -> Unit
) {
    val icon = rememberPhotoMarker(marker.imageUrl)
    Marker(
        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
        title = marker.title,
        snippet = marker.price?.let { "$ %.0f".format(it) },
        icon = icon,
        onClick = {
            onMarkerClick(marker.id)
            true
        }
    )
}
