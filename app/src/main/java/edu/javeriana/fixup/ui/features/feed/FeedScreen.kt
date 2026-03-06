package edu.javeriana.fixup.ui.features.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.*
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    onPublicationClick: (String) -> Unit = {},
    onAllPublicationsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // SEARCH BAR
        item {
            SearchBar(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // TITULO 1
        item {
            Text(
                text = "Remodelaciones recomendadas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // IMAGEN DESTACADA
        item {
            FeaturedImage(
                imageRes = R.drawable.featured_image
            )
        }

        // TITULO CATEGORIAS
        item {
            SectionTitle(
                text = "Categorias",
                showArrow = true
            )
        }

        // CATEGORIAS
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.categories) { category ->
                    CategoryItem(
                        imageRes = category.imageRes,
                        title = category.title
                    )
                }
            }
        }

        // TITULO PUBLICACIONES
        item {
            SectionTitle(
                text = "Publicaciones",
                showArrow = true,
                modifier = Modifier.clickable { onAllPublicationsClick() }
            )
        }

        // PUBLICACIONES
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.publications) { publication ->
                    PublicationCard(
                        imageRes = publication.imageRes,
                        title = publication.title,
                        price = publication.price,
                        onClick = { onPublicationClick(publication.id) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FixUpTheme {
        FeedScreen()
    }
}
