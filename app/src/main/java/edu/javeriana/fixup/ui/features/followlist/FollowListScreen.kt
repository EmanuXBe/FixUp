package edu.javeriana.fixup.ui.features.followlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.javeriana.fixup.ui.model.FollowUser
import edu.javeriana.fixup.ui.theme.SoftFawn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    onNavigateBack: () -> Unit,
    onUserClick: (String) -> Unit = {},
    viewModel: FollowListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val title = when (uiState.listType) {
        FollowListType.FOLLOWERS -> "Seguidores"
        FollowListType.FOLLOWING -> "Siguiendo"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = SoftFawn) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = SoftFawn
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )

                uiState.users.isEmpty() -> Text(
                    text = if (uiState.listType == FollowListType.FOLLOWERS)
                        "Este usuario aún no tiene seguidores"
                    else "Este usuario no sigue a nadie aún",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items = uiState.users, key = { it.uid }) { user ->
                        FollowUserItem(user = user, onClick = { onUserClick(user.uid) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowUserItem(user: FollowUser, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(user.displayName, color = SoftFawn) },
        supportingContent = { Text("@${user.username}") },
        leadingContent = {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    )
}