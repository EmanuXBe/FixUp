package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.model.MockPropertyRepository
import edu.javeriana.fixup.ui.theme.FixUpTheme
import java.text.NumberFormat
import java.util.*

@Composable
fun PropertyDetailScreen(
    propertyId: String? = null,
    onBackClick: () -> Unit,
    onReserveClick: () -> Unit
) {
    // Buscamos la propiedad por ID. Si no se encuentra o el ID es nulo, usamos un valor por defecto o manejamos el error.
    val property = remember(propertyId) {
        MockPropertyRepository.getProperties().find { it.id == propertyId }
    }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Propiedad no encontrada")
                Button(onClick = onBackClick) {
                    Text("Volver")
                }
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currencyFormat.format(property.price)} $",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Por mes",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = onReserveClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Reservar ahora")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Image and Back button
            Box(modifier = Modifier.height(300.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(property.imageUrl)
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
                        .background(Color.White, RoundedCornerShape(50.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(50.dp))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(50.dp))
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bogotá, Colombia", // Podrías agregar ubicación al modelo si fuera necesario
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PropertyFeature(text = "${property.bedrooms} Habitaciones")
                    PropertyFeature(text = "${property.bathrooms} Baños")
                    if (property.hasParking) {
                        PropertyFeature(text = "1 Parqueadero")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Descripción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = property.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Lo que ofrece este lugar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Aquí podrías listar amenidades adicionales si el modelo las tuviera
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Cocina integral", fontSize = 14.sp)
                Text("• Wifi de alta velocidad", fontSize = 14.sp)
                Text("• Zona de lavandería", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PropertyFeature(text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyDetailScreenPreview() {
    FixUpTheme {
        PropertyDetailScreen(propertyId = "1", onBackClick = {}, onReserveClick = {})
    }
}
