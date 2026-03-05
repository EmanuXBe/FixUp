package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.SoftFawn
import edu.javeriana.fixup.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationScreen(
    publicationId: String? = null,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    feedViewModel: FeedViewModel = viewModel()
) {
    val uiState by feedViewModel.uiState.collectAsState()
    val publication = uiState.publications.find { it.id == publicationId } ?: uiState.publications.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Publicación") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = publication.imageRes),
                    contentDescription = "Imagen de la publicación",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = publication.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = publication.price,
                    fontSize = 20.sp,
                    color = SoftFawn,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Descripción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                val description = when(publicationId) {
                    "1" -> "Transforma tu espacio con nuestros diseños exclusivos de salas. Utilizamos materiales de alta calidad y nos adaptamos a tus necesidades y presupuesto."
                    "2" -> "¡Arma el comedor de tus sueños! Ofrecemos soluciones integrales para que tus cenas familiares sean inolvidables."
                    "3" -> "Renovamos tu baño por completo. Incluye cambio de sanitarios, grifería de alta gama y revestimientos modernos."
                    "4" -> "Cocina integral con acabados premium. Optimización de espacio y diseño ergonómico para tu comodidad."
                    else -> "Transforma tu espacio con nuestros diseños exclusivos. Utilizamos materiales de alta calidad y nos adaptamos a tus necesidades."
                }

                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onContactClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
                ) {
                    Text(text = "Contactar Especialista", fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationScreenPreview() {
    FixUpTheme {
        PublicationScreen(publicationId = "1", onBackClick = {}, onContactClick = {})
    }
}
