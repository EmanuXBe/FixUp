package edu.javeriana.fixup.ui.features.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onAllPublicationsClick: () -> Unit = {}
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SearchBar(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                FeaturedSection()
            }

            item {
                CategoriesSection(categories = uiState.categories)
            }

            item {
                PublicationsSection(
                    publications = uiState.publications,
                    onAllPublicationsClick = onAllPublicationsClick,
                    onPublicationClick = onPublicationClick
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FeaturedSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Remodelaciones recomendadas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
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

@Composable
private fun PublicationsSection(
    publications: List<PublicationCardModel>,
    onAllPublicationsClick: () -> Unit,
    onPublicationClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(
            text = "Publicaciones",
            showArrow = true,
            modifier = Modifier.clickable { onAllPublicationsClick() }
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(publications) { publication ->
                PublicationCard(
                    imageRes = publication.imageUrl,
                    title = publication.title,
                    price = publication.price,
                    onClick = { onPublicationClick(publication.id) }
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
