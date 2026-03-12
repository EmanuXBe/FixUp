package edu.javeriana.fixup.ui.features.publication_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun PublicationDetailScreen(
    publicationId: String? = null,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    viewModel: PublicationDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
    }

    when {
        uiState.isLoading -> LoadingState()
        uiState.publication != null -> PublicationContent(
            publication = uiState.publication!!,
            description = uiState.description,
            onContactClick = onContactClick
        )
        else -> ErrorState(
            errorText = uiState.error ?: "Publicación no encontrada",
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
            Text(errorText)
            Button(onClick = onBackClick) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun PublicationContent(
    publication: PublicationCardModel,
    description: String,
    onContactClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            PublicationHeader(publication, description)
        }

        item {
            FixerSection()
        }

        item {
            BenefitsSection()
        }

        item {
            ActionButtons(onContactClick)
        }
    }
}

@Composable
private fun PublicationHeader(publication: PublicationCardModel, description: String) {
    Column {
        AsyncImage(
            model = publication.imageRes,
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FixerSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = R.drawable.profile_photo,
                    contentDescription = "Fixer Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingStars(rating = 4)
                Text(
                    text = "Destacados",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Sobre tu fixer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam commodo nunc nulla, ut rutrum nulla sodales id.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RatingStars(rating: Int) {
    Row {
        repeat(rating) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(18.dp)
            )
        }
        repeat(5 - rating) {
            Icon(
                Icons.Default.StarBorder,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun BenefitsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BenefitItem(icon = Icons.Outlined.AccessTime, text = "Puntual")
            BenefitItem(icon = Icons.Outlined.Bolt, text = "Eficaz")
            BenefitItem(icon = Icons.Outlined.Payments, text = "Economico")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ActionButtons(onContactClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onContactClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
        ) {
            Text(text = "Ir al Pago", fontSize = 16.sp)
        }

        OutlinedButton(
            onClick = { /* Acción para contactar especialista */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftFawn),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftFawn)
        ) {
            Text(text = "Contactar Especialista", fontSize = 16.sp)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun BenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationDetailScreenPreview() {
    FixUpTheme {
        PublicationDetailScreen(publicationId = "1", onBackClick = {}, onContactClick = {})
    }
}
