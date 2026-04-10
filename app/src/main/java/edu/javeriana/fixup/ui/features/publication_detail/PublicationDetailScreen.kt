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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.theme.SoftFawn

private const val MY_USER_ID = 1

@Composable
fun PublicationDetailScreen(
    publicationId: String? = null,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    viewModel: PublicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(publicationId) { viewModel.loadPublication(publicationId) }

    LaunchedEffect(uiState.reviewSent) {
        if (uiState.reviewSent) snackbarHostState.showSnackbar("¡Review guardada!")
    }
    LaunchedEffect(uiState.reviewError) {
        uiState.reviewError?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SoftFawn)
                }
                uiState.publication != null -> PublicationContent(
                    publication = uiState.publication!!,
                    description = uiState.description,
                    reviews = uiState.reviews,
                    isSendingReview = uiState.isSendingReview,
                    onBackClick = onBackClick,
                    onContactClick = onContactClick,
                    onSendReview = { rating, comment -> viewModel.sendReview(rating, comment) },
                    onEditReview = { review -> viewModel.openEditDialog(review) },
                    onDeleteReview = { reviewId -> viewModel.deleteReview(reviewId) }
                )
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error ?: "Publicación no encontrada", textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }

    // Dialog de edición
    if (uiState.showEditDialog && uiState.editingReview != null) {
        EditReviewDialog(
            review = uiState.editingReview!!,
            isSaving = uiState.isSendingReview,
            onConfirm = { rating, comment ->
                viewModel.updateReview(uiState.editingReview!!.idAsString, rating, comment)
            },
            onDismiss = { viewModel.closeEditDialog() }
        )
    }
}

@Composable
private fun PublicationContent(
    publication: PublicationCardModel,
    description: String,
    reviews: List<ReviewModel>,
    isSendingReview: Boolean,
    onBackClick: () -> Unit,
    onContactClick: () -> Unit,
    onSendReview: (Int, String) -> Unit,
    onEditReview: (ReviewModel) -> Unit,
    onDeleteReview: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
        item {
            AsyncImage(
                model = publication.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Info publicación
                Text(publication.title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(publication.price, fontSize = 22.sp, color = SoftFawn, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("Descripción", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(24.dp))
                FixerSection()
                Spacer(Modifier.height(24.dp))
                BenefitsSection()
                Spacer(Modifier.height(24.dp))

                // Sección agregar review
                AddReviewSection(isSendingReview, onSendReview)
                Spacer(Modifier.height(24.dp))

                // Lista de reviews con editar/eliminar
                ReviewsSection(
                    reviews = reviews,
                    onEditReview = onEditReview,
                    onDeleteReview = onDeleteReview
                )
                Spacer(Modifier.height(24.dp))

                // Botones de acción
                Button(
                    onClick = onContactClick,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
                ) { Text("Ir al Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun AddReviewSection(isSending: Boolean, onSendReview: (Int, String) -> Unit) {
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
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RateReview, contentDescription = null, tint = SoftFawn)
                    Spacer(Modifier.width(8.dp))
                    Text("¿Cómo fue tu experiencia?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            AnimatedVisibility(visible = isExpanded, enter = fadeIn(tween(300)), exit = fadeOut(tween(300))) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        repeat(5) { index ->
                            IconButton(onClick = { rating = index + 1 }) {
                                Icon(
                                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (index < rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cuéntanos más detalles...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftFawn, cursorColor = SoftFawn)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { if (comment.isNotBlank()) { onSendReview(rating, comment); comment = ""; isExpanded = false } },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = !isSending && comment.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
                    ) {
                        if (isSending) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Publicar Reseña", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection(
    reviews: List<ReviewModel>,
    onEditReview: (ReviewModel) -> Unit,
    onDeleteReview: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Opiniones de la comunidad", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            if (reviews.isNotEmpty()) {
                Surface(color = SoftFawn.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(reviews.size.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = SoftFawn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                    Text("Aún no hay comentarios. ¡Sé el primero!", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            reviews.forEach { review ->
                ReviewItem(review = review, onEditReview = onEditReview, onDeleteReview = onDeleteReview)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: ReviewModel,
    onEditReview: (ReviewModel) -> Unit,
    onDeleteReview: (String) -> Unit
) {
    val isMyReview = review.userId == MY_USER_ID

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Avatar con inicial
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(SoftFawn.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.displayName.take(1).uppercase(),
                        color = SoftFawn,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(review.displayName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < review.rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                // Botones editar/eliminar solo para mis reviews (userId == 1)
                if (isMyReview) {
                    IconButton(onClick = { onEditReview(review) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = SoftFawn, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { onDeleteReview(review.idAsString) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                    }
                }
            }
            if (review.comment.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(review.comment, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun EditReviewDialog(
    review: ReviewModel,
    isSaving: Boolean,
    onConfirm: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableIntStateOf(review.rating) }
    var comment by remember { mutableStateOf(review.comment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar review") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Calificación", fontWeight = FontWeight.Medium)
                Row {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFC107) else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftFawn, cursorColor = SoftFawn)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(rating, comment) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = SoftFawn)
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun FixerSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = R.drawable.profile_photo,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Tu Especialista FixUp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Verificado • 4.8 ★", color = SoftFawn, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Profesional con más de 5 años de experiencia.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BenefitsSection() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        BenefitItem(icon = Icons.Outlined.VerifiedUser, text = "Garantía")
        BenefitItem(icon = Icons.Outlined.Bolt, text = "Rápido")
        BenefitItem(icon = Icons.Outlined.SupportAgent, text = "Soporte")
    }
}

@Composable
fun BenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(56.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = SoftFawn)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}