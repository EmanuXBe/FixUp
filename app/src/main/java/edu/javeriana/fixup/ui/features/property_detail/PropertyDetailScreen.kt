package edu.javeriana.fixup.ui.features.property_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
            reviews = uiState.reviews,
            newComment = uiState.newReviewComment,
            newRating = uiState.newReviewRating,
            isPosting = uiState.isPostingReview,
            onCommentChange = viewModel::onCommentChange,
            onRatingChange = viewModel::onRatingChange,
            onPostReview = viewModel::postReview,
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
                Text(text = "Volver")
            }
        }
    }
}

@Composable
private fun PropertyContent(
    property: PropertyModel,
    reviews: List<ReviewModel>,
    newComment: String,
    newRating: Int,
    isPosting: Boolean,
    onCommentChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onPostReview: () -> Unit,
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

        // Sección para añadir nuevo comentario
        AddReviewSection(
            comment = newComment,
            rating = newRating,
            isPosting = isPosting,
            onCommentChange = onCommentChange,
            onRatingChange = onRatingChange,
            onPostReview = onPostReview
        )

        // Sección de reseñas listadas
        ReviewsSection(reviews = reviews)

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onReserveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            val priceDisplay = property.price?.let { String.format("%.2f", it) } ?: "0.00"
            Text("Reservar ahora por $$priceDisplay")
        }
    }
}

@Composable
private fun AddReviewSection(
    comment: String,
    rating: Int,
    isPosting: Boolean,
    onCommentChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onPostReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¿Qué te pareció este servicio?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Selector de estrellas táctil
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onRatingChange(index + 1) },
                        tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe tu experiencia aquí...") },
                maxLines = 3,
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onPostReview,
                enabled = !isPosting && comment.isNotBlank(),
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar")
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection(reviews: List<ReviewModel>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Reseñas de otros usuarios",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (reviews.isEmpty()) {
            Text(
                text = "Aún no hay reseñas para este artículo. ¡Sé el primero en comentar!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            reviews.forEach { review ->
                ReviewItem(review)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun ReviewItem(review: ReviewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (review.userName.isNullOrBlank()) "Usuario FixUp" else review.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            RatingStars(rating = review.rating)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = review.comment,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = review.date,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RatingStars(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        }
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
        Text(
            text = "Descripción",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = property.description ?: "Sin descripción",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyDetailScreenPreview() {
    FixUpTheme {
        PropertyDetailScreen(onBackClick = {}, onReserveClick = {})
    }
}
