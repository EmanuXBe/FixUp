package edu.javeriana.fixup.ui.features.property_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import edu.javeriana.fixup.data.util.AppConstants
import edu.javeriana.fixup.ui.model.PropertyModel
import edu.javeriana.fixup.ui.model.ReviewModel
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
            uiState = uiState,
            onBackClick = onBackClick,
            onReserveClick = onReserveClick,
            onSaveReview = { rating, comment -> 
                uiState.property?.id?.let { id ->
                    viewModel.saveReview(id, rating, comment)
                }
            },
            onUpdateReview = { reviewId, rating, comment ->
                uiState.property?.id?.let { id ->
                    viewModel.updateReview(reviewId, id, rating, comment)
                }
            },
            onDeleteReview = { reviewId ->
                uiState.property?.id?.let { id ->
                    viewModel.deleteReview(reviewId, id)
                }
            }
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
    uiState: PropertyDetailUiState,
    onBackClick: () -> Unit,
    onReserveClick: () -> Unit,
    onSaveReview: (Int, String) -> Unit,
    onUpdateReview: (String, Int, String) -> Unit,
    onDeleteReview: (String) -> Unit
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

        Spacer(modifier = Modifier.height(24.dp))

        ReviewsSection(
            reviews = uiState.reviews,
            isSaving = uiState.isSavingReview,
            onSaveReview = onSaveReview,
            onUpdateReview = onUpdateReview,
            onDeleteReview = onDeleteReview
        )

        Spacer(modifier = Modifier.height(40.dp))
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
            text = property.title ?: "Sin título",
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
                text = property.location ?: "Sin ubicación",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PropertyDescription(description = property.description ?: "Sin descripción")

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
private fun ReviewsSection(
    reviews: List<ReviewModel>,
    isSaving: Boolean,
    onSaveReview: (Int, String) -> Unit,
    onUpdateReview: (String, Int, String) -> Unit,
    onDeleteReview: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reseñas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { showAddDialog = true }) {
                Text("Dejar Reseña")
            }
        }

        if (showAddDialog) {
            AddReviewDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { rating, comment ->
                    onSaveReview(rating, comment)
                    showAddDialog = false
                }
            )
        }

        if (reviews.isEmpty()) {
            Text(
                text = "Aún no hay reseñas para este lugar.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reviews.forEach { review ->
                ReviewItem(
                    review = review,
                    onEdit = { rating, comment -> onUpdateReview(review.id, rating, comment) },
                    onDelete = { onDeleteReview(review.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ReviewModel,
    onEdit: (Int, String) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AddReviewDialog(
            initialRating = review.rating,
            initialComment = review.comment,
            isEditing = true,
            onDismiss = { showEditDialog = false },
            onConfirm = { rating, comment ->
                onEdit(rating, comment)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (review.userId == AppConstants.CURRENT_USER_ID) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = review.comment, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = review.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AddReviewDialog(
    initialRating: Int = 5,
    initialComment: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Reseña" else "Nueva Reseña") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }) {
                            Icon(
                                imageVector = if (index < rating) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = Color(0xFFFFB300)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("¿Qué te pareció el lugar?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(rating, comment) },
                enabled = comment.isNotBlank()
            ) {
                Text(if (isEditing) "Guardar" else "Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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
