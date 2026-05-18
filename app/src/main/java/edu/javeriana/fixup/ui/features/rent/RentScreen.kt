package edu.javeriana.fixup.ui.features.rent

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.PropertyCard
import edu.javeriana.fixup.ui.model.PropertyModel

@Composable
fun RentScreen(
    viewModel: RentViewModel = hiltViewModel(),
    onSelectClick: (String) -> Unit,
    onCreateClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Cuando el ViewModel pide GPS, lanzamos el picker de permisos del sistema
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        val ok = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                 granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (ok) requestLocationFix(context, viewModel)
        else viewModel.onLocationPermissionDenied()
    }

    val successState = uiState as? RentUiState.Success
    LaunchedEffect(successState?.needsLocationPermission, successState?.sort) {
        if (successState?.sort == RentSort.NEARBY && successState.userLocation == null) {
            val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
                requestLocationFix(context, viewModel)
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Publicar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            RentHeader()

            when (val state = uiState) {
                is RentUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RentUiState.Success -> {
                    RentContent(
                        properties         = state.properties,
                        currentSort        = state.sort,
                        onSortChange       = viewModel::setSort,
                        onPropertySelected = { id -> onSelectClick(id) },
                        onMapClick         = onMapClick
                    )
                }
                is RentUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission") // El permiso se verifica antes de llamar a esta función
private fun requestLocationFix(
    context: android.content.Context,
    viewModel: RentViewModel
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                viewModel.setUserLocation(location.latitude, location.longitude)
            }
        }
}

@Composable
fun RentContent(
    properties: List<PropertyModel>,
    currentSort: RentSort,
    onSortChange: (RentSort) -> Unit,
    onPropertySelected: (String) -> Unit,
    onMapClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FilterControls(
                resultCount = properties.size,
                currentSort = currentSort,
                onSortChange = onSortChange
            )
        }

        item {
            MapAreaPlaceholder(modifier = Modifier.height(240.dp), onClick = onMapClick)
        }

        items(
            items = properties,
            key = { it.id ?: it.hashCode() }
        ) { property ->
            PropertyCard(
                property = property,
                onSelectClick = { property.id?.let { onPropertySelected(it) } },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun RentHeader(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Apartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Explorar Inmuebles",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Inmuebles y arreglos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterControls(
    modifier: Modifier = Modifier,
    resultCount: Int,
    currentSort: RentSort,
    onSortChange: (RentSort) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ordenar por", fontWeight = FontWeight.SemiBold)
            Text(text = "$resultCount resultados", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                SortChip(
                    label = "Precio ↑",
                    icon = Icons.Outlined.AttachMoney,
                    selected = currentSort == RentSort.PRICE_ASC,
                    onClick = {
                        onSortChange(if (currentSort == RentSort.PRICE_ASC) RentSort.NONE else RentSort.PRICE_ASC)
                    }
                )
            }
            item {
                SortChip(
                    label = "Precio ↓",
                    icon = Icons.Outlined.AttachMoney,
                    selected = currentSort == RentSort.PRICE_DESC,
                    onClick = {
                        onSortChange(if (currentSort == RentSort.PRICE_DESC) RentSort.NONE else RentSort.PRICE_DESC)
                    }
                )
            }
            item {
                SortChip(
                    label = "Más recientes",
                    icon = Icons.Outlined.Schedule,
                    selected = currentSort == RentSort.DATE_DESC,
                    onClick = {
                        onSortChange(if (currentSort == RentSort.DATE_DESC) RentSort.NONE else RentSort.DATE_DESC)
                    }
                )
            }
            item {
                SortChip(
                    label = "Cerca de mí",
                    icon = Icons.Outlined.MyLocation,
                    selected = currentSort == RentSort.NEARBY,
                    onClick = {
                        onSortChange(if (currentSort == RentSort.NEARBY) RentSort.NONE else RentSort.NEARBY)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    )
}

@Composable
fun MapAreaPlaceholder(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E7FF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.5f
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.55f)
        ) {
            Text(
                text = "Abrir mapa →",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
