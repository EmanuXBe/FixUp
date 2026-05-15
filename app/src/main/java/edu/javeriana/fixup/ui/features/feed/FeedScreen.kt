package edu.javeriana.fixup.ui.features.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.*
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onPublicationClick: (String) -> Unit = {},
    onAllPublicationsClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {},
    onAssistantClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isConnected) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.padding(16.dp)
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NoConnectionMessage()
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize().testTag("feed_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBar(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                FeaturedSection(
                    onFollowingClick = onFollowingClick,
                    onAssistantClick = onAssistantClick,
                    onSeedClick = { viewModel.seedData() }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoriesSection(categories = uiState.categories)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(
                    text = "Publicaciones",
                    showArrow = true,
                    modifier = Modifier
                        .clickable { onAllPublicationsClick() }
                        .padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(uiState.publications) { publication ->
                PublicationCard(
                    imageRes = publication.imageUrl,
                    title = publication.title,
                    price = publication.price,
                    modifier = Modifier.testTag("publication_card"),
                    onClick = { onPublicationClick(publication.id) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeaturedSection(
    onFollowingClick: () -> Unit,
    onAssistantClick: () -> Unit,
    onSeedClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recomendados",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Botón de Pánico para Testing
            if (edu.javeriana.fixup.BuildConfig.DEBUG) {
                Button(
                    onClick = onSeedClick,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text("Sembrar", fontSize = 12.sp)
                }
            }

            TextButton(
                onClick = onAssistantClick,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Asistente", fontSize = 13.sp)
            }

            TextButton(onClick = onFollowingClick) {
                Text("Siguiendo")
            }
        }
        FeaturedImage(imageRes = R.drawable.featured_image)
    }
}

@Composable
private fun CategoriesSection(categories: List<CategoryItemModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(text = "Categorias", showArrow = true)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { category ->
                CategoryItem(
                    imageRes = category.imageRes,
                    title = category.title
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FixUpTheme {
        FeedScreen()
    }
}
