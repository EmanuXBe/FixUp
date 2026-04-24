package edu.javeriana.fixup.ui.features.user_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.model.UserModel
import edu.javeriana.fixup.ui.theme.SoftFawn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String?,
    onBackClick: () -> Unit,
    onServiceClick: (String) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var followDialogType by remember { mutableStateOf<FollowDialogType?>(null) }

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    followDialogType?.let { dialogType ->
        FollowUsersDialog(
            title = if (dialogType == FollowDialogType.FOLLOWERS) "Seguidores" else "Seguidos",
            users = if (dialogType == FollowDialogType.FOLLOWERS) uiState.followersUsers else uiState.followingUsers,
            isLoading = uiState.isFollowListLoading,
            onDismiss = { followDialogType = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null && uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "Error desconocido")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    val currentUserId = viewModel.getCurrentUserId()
                    UserHeader(
                        user = uiState.user,
                        isCurrentUser = currentUserId != null && currentUserId == uiState.user?.id,
                        isFollowing = currentUserId != null && uiState.user?.followers?.contains(currentUserId) == true,
                        onFollowClick = { viewModel.toggleFollow() },
                        onFollowersClick = {
                            followDialogType = FollowDialogType.FOLLOWERS
                            viewModel.loadFollowersUsers()
                        },
                        onFollowingClick = {
                            followDialogType = FollowDialogType.FOLLOWING
                            viewModel.loadFollowingUsers()
                        }
                    )
                }

                item {
                    Text(
                        text = "Reseñas realizadas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftFawn
                    )
                }

                if (uiState.reviews.isEmpty()) {
                    item {
                        Text(
                            text = "Este usuario aún no ha realizado reseñas.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(uiState.reviews) { review ->
                        UserReviewItem(
                            review = review,
                            currentUserId = viewModel.getCurrentUserId(),
                            onLikeClick = { viewModel.toggleLikeReview(review.id) },
                            onClick = { onServiceClick(review.serviceId) }
                        )
                    }
                }
            }
        }
    }
}

private enum class FollowDialogType {
    FOLLOWERS,
    FOLLOWING
}

@Composable
private fun UserHeader(
    user: UserModel?,
    isCurrentUser: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.profileImageUrl?.takeIf { it.isNotBlank() },
            contentDescription = "Foto de perfil",
            placeholder = painterResource(id = R.drawable.profile_photo),
            error = painterResource(id = R.drawable.profile_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SoftFawn
        )

        Text(
            text = user.role,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onFollowersClick() }
            ) {
                Text(text = user.followers.size.toString(), fontWeight = FontWeight.Bold)
                Text(text = "Seguidores", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onFollowingClick() }
            ) {
                Text(text = user.following.size.toString(), fontWeight = FontWeight.Bold)
                Text(text = "Seguidos", fontSize = 12.sp)
            }
        }

        if (!isCurrentUser) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onFollowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else SoftFawn,
                    contentColor = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Text(text = if (isFollowing) "Dejar de seguir" else "Seguir")
            }
        }
    }
}

@Composable
private fun FollowUsersDialog(
    title: String,
    users: List<UserModel>,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                users.isEmpty() -> {
                    Text("No hay usuarios para mostrar.")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                    ) {
                        items(users) { user ->
                            FollowUserItem(user = user)
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun FollowUserItem(user: UserModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profileImageUrl?.takeIf { it.isNotBlank() },
            contentDescription = "Foto de perfil",
            placeholder = painterResource(id = R.drawable.profile_photo),
            error = painterResource(id = R.drawable.profile_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.name.ifBlank { "Usuario sin nombre" },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = user.email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun UserReviewItem(
    review: ReviewModel,
    currentUserId: String?,
    onLikeClick: () -> Unit,
    onClick: () -> Unit
) {
    val isLiked = currentUserId != null && review.likedBy.contains(currentUserId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(review.authorProfileImageUrl.ifBlank { R.drawable.profile_photo })
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de ${review.authorName}",
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
                            fontWeight = FontWeight.Bold,
                            color = SoftFawn
                        )
                        Text(
                            text = "comentó sobre: ${review.serviceTitle.ifBlank { "un servicio" }}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text = review.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray,
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

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
