package edu.javeriana.fixup.ui.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import edu.javeriana.fixup.componentsUtils.SearchBar
import edu.javeriana.fixup.componentsUtils.SectionTitle
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun AllPublicationsScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onPublicationClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle(
            text = "Publicaciones",
            showArrow = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.publications) { publication ->
                VerticalPublicationItem(
                    imageRes = publication.imageRes,
                    title = publication.title,
                    price = publication.price,
                    onClick = { onPublicationClick(publication.id) }
                )
            }
        }
    }
}

@Composable
fun VerticalPublicationItem(
    imageRes: Int,
    title: String,
    price: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageRes,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Categoría ficticia según el título para que se parezca a la foto
        val category = when {
            title.contains("Sala", ignoreCase = true) -> "Salas"
            title.contains("Comedor", ignoreCase = true) -> "Comedores"
            title.contains("Cocina", ignoreCase = true) -> "Cocina"
            title.contains("Baño", ignoreCase = true) -> "Baños"
            else -> "Remodelación"
        }
        
        Text(
            text = category,
            fontSize = 12.sp,
            color = Color.Gray
        )
        
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = price,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllPublicationsScreenPreview() {
    FixUpTheme {
        AllPublicationsScreen()
    }
}
