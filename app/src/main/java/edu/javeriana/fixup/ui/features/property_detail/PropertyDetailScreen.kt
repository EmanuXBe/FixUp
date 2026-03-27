package edu.javeriana.fixup.ui.features.property_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun PropertyDetailScreen(
    propertyId: String? = null,
    onBackClick: () -> Unit,
    onReserveClick: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    when {
        uiState.isLoading -> LoadingState()
        uiState.property != null -> PropertyContent(
            property = uiState.property!!,
            onBackClick = onBackClick,
            onReserveClick = onReserveClick
        )
        else -> ErrorState(
            errorText = uiState.error ?: "Propiedad no encontrada",
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(errorText: String, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick) {
                Text(
                    text = "Volver",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PropertyContent(
    property: PropertyModel,
    onBackClick: () -> Unit,
    onReserveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        PropertyHeader(
            imageUrl = property.imageUrl ?: "",
            onBackClick = onBackClick
        )

        PropertyDetails(property = property)
    }
}

@Composable
private fun PropertyHeader(
    imageUrl: String,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.height(300.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Property Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.chapi)
        )
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    RoundedCornerShape(50.dp)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            ActionIconButton(icon = Icons.Default.Share, contentDescription = "Share")
            Spacer(modifier = Modifier.width(8.dp))
            ActionIconButton(icon = Icons.Default.FavoriteBorder, contentDescription = "Favorite")
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = { },
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                RoundedCornerShape(50.dp)
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PropertyDetails(property: PropertyModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = property.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = property.location,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PropertyDescription(description = property.description)

        Spacer(modifier = Modifier.height(24.dp))

        PropertyAmenities()
    }
}

@Composable
private fun PropertyDescription(description: String) {
    Text(
        text = "Descripción",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = description,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 20.sp
    )
}

@Composable
private fun PropertyAmenities() {
    Text(
        text = "Lo que ofrece este lugar",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))
    AmenityText("• Servicio de alta calidad")
    AmenityText("• Atención personalizada")
    AmenityText("• Garantía FixUp")
}

@Composable
private fun AmenityText(text: String) {
    Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PropertyDetailScreenPreview() {
    FixUpTheme {
        PropertyDetailScreen(propertyId = "1", onBackClick = {}, onReserveClick = {})
    }
}
