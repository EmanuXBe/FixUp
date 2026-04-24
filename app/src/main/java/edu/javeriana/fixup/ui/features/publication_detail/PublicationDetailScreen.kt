package edu.javeriana.fixup.ui.features.publication_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun PublicationDetailScreen(
    publicationId: String? = null,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    onUserProfileClick: (String) -> Unit = {},
    viewModel: PublicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
    }

    LaunchedEffect(uiState.reviewSent) {
        if (uiState.reviewSent) {
            snackbarHostState.showSnackbar("¡Gracias! Tu reseña ha sido publicada.")
        }
    }

    LaunchedEffect(uiState.reviewError) {
        uiState.reviewError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.publication != null -> PublicationContent(
                    publication = uiState.publication!!,
                    description = uiState.description,
                    reviews = uiState.reviews,
                    isSendingReview = uiState.isSendingReview,
                    currentUserId = viewModel.getCurrentUserId(),
                    onBackClick = onBackClick,
                    onContactClick = onContactClick,
                    onUserProfileClick = onUserProfileClick,
                    onSendReview = { rating, comment ->
                        viewModel.sendReview(rating, comment)
                    },
                    onLikeClick = { reviewId -> viewModel.toggleLikeReview(reviewId) }
                )
                else -> ErrorState(
                    errorText = uiState.error ?: "Publicación no encontrada",
                    onBackClick = onBackClick
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SoftFawn)
    }
}

@Composable
private fun ErrorState(errorText: String, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(errorText, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun PublicationContent(
    publication: PublicationCardModel,
    description: String,
    reviews: List<ReviewModel>,
    isSendingReview: Boolean,
    currentUserId: String?,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    onUserProfileClick: (String) -> Unit,
    onSendReview: (Int, String) -> Unit,
    onLikeClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                AsyncImage(
                    model = publication.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    PublicationInfo(publication, description)
                    Spacer(modifier = Modifier.height(24.dp))
                    FixerSection()
                    Spacer(modifier = Modifier.height(24.dp))
                    BenefitsSection()
                    Spacer(modifier = Modifier.height(24.dp))
                    AddReviewSection(isSendingReview, onSendReview)
                    Spacer(modifier = Modifier.height(24.dp))
                    ReviewsSection(
                        reviews = reviews,
                        currentUserId = currentUserId,
                        onLikeClick = onLikeClick,
                        onUserProfileClick = onUserProfileClick
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ActionButtons(onContactClick)
                }
            }
        }
    }
}

@Composable
private fun PublicationInfo(publication: PublicationCardModel, description: String) {
    Column {
        Text(
            text = publication.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = publication.price,
            fontSize = 22.sp,
            color = SoftFawn,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Descripción",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddReviewSection(
    isSending: Boolean,
    onSendReview: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RateReview, contentDescription = null, tint = SoftFawn)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¿Cómo fue tu experiencia?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            val isSelected = index < rating
                            IconButton(onClick = { rating = index + 1 }) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cuéntanos más detalles del servicio...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftFawn,
                            cursorColor = SoftFawn
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (comment.isNotBlank()) {
                                onSendReview(rating, comment)
                                comment = ""
                                isExpanded = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = !isSending && comment.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Publicar Reseña", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection(
    reviews: List<ReviewModel>,
    currentUserId: String?,
    onLikeClick: (String) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Opiniones de la comunidad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (reviews.isNotEmpty()) {
                Surface(
                    color = SoftFawn.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = reviews.size.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = SoftFawn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (reviews.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "Aún no hay comentarios. ¡Sé el primero!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            reviews.forEach { review ->
                ReviewItem(
                    review = review,
                    currentUserId = currentUserId,
                    onLikeClick = { onLikeClick(review.id) },
                    onUserProfileClick = onUserProfileClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ReviewModel,
    currentUserId: String?,
    onLikeClick: () -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    val isLiked = currentUserId != null && review.likedBy.contains(currentUserId)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUserProfileClick(review.userId) }.weight(1f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(review.authorProfileImageUrl.ifBlank { R.drawable.profile_photo })
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil de ${review.authorName}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.profile_photo),
                        placeholder = painterResource(id = R.drawable.profile_photo)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = review.authorName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftFawn
                        )
                        if (review.serviceTitle.isNotEmpty()) {
                            Text(
                                text = "comentó sobre: ${review.serviceTitle}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < review.rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = review.date,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = review.likedBy.size.toString(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun FixerSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.profile_photo,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Tu Especialista FixUp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Verificado • 4.8 ★", color = SoftFawn, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Profesional con más de 5 años de experiencia en servicios para el hogar.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BenefitsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BenefitItem(icon = Icons.Outlined.VerifiedUser, text = "Garantía")
        BenefitItem(icon = Icons.Outlined.Bolt, text = "Rápido")
        BenefitItem(icon = Icons.Outlined.SupportAgent, text = "Soporte")
    }
}

@Composable
private fun ActionButtons(onContactClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onContactClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
        ) {
            Text(text = "Ir al Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftFawn),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftFawn)
        ) {
            Text(text = "Contactar Especialista", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = SoftFawn)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationDetailScreenPreview() {
    FixUpTheme {
        PublicationDetailScreen(publicationId = "1", onBackClick = {}, onContactClick = {})
    }
}
