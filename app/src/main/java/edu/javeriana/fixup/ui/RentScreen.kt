package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.rent.RentUiState
import edu.javeriana.fixup.ui.features.rent.RentViewModel
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.theme.FixUpTheme
import java.text.NumberFormat
import java.util.*

@Composable
fun RentScreen(
    viewModel: RentViewModel = viewModel(),
    onSelectClick: (String) -> Unit,
    onCreateClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        properties = state.properties,
                        onPropertySelected = { id ->
                            viewModel.onPropertySelected(id)
                            onSelectClick(id)
                        }
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

@Composable
fun RentContent(
    properties: List<PropertyModel>,
    onPropertySelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FilterControls(resultCount = properties.size)
        }

        item {
            MapAreaPlaceholder(
                properties = properties,
                modifier = Modifier.height(240.dp)
            )
        }

        items(properties) { property ->
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
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Explorar Servicios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Inmuebles y arreglos para tu hogar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun FilterControls(modifier: Modifier = Modifier, resultCount: Int) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton(text = "Filtro", icon = Icons.Outlined.Tune)
        }
        Text(
            text = "$resultCount resultados",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FilterButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PropertyCard(
    property: PropertyModel,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.sala)
                )

                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd).size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = property.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = property.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = property.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currencyFormat.format(property.price)} $",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = onSelectClick, shape = RoundedCornerShape(12.dp)) {
                        Text("Seleccionar")
                    }
                }
            }
        }
    }
}

@Composable
fun MapAreaPlaceholder(
    properties: List<PropertyModel>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E7FF))
    ) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.5f
        )
        
        // Tags de precio estáticos para el diseño
        PriceTag(price = 1500000.0, isSelected = false, modifier = Modifier.offset(x = 50.dp, y = 80.dp))
        PriceTag(price = 2100000.0, isSelected = false, modifier = Modifier.offset(x = 200.dp, y = 50.dp))
        PriceTag(price = 1200000.0, isSelected = true, modifier = Modifier.offset(x = 180.dp, y = 120.dp))
    }
}

@Composable
private fun PriceTag(price: Double, isSelected: Boolean, modifier: Modifier = Modifier) {
    val displayPrice = "$${(price / 1000000).format(1)}M"
    Surface(
        modifier = modifier,
        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Text(
            text = displayPrice,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Preview(showBackground = true)
@Composable
fun RentScreenPreview() {
    FixUpTheme {
        RentScreen(onSelectClick = {})
    }
}
