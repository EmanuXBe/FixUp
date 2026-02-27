package edu.javeriana.fixup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.*
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun FeedScreen(
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onPublicationClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = BrightSnow,
        bottomBar = {
            BottomNavBar(
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick,
                currentScreen = "home"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SEARCH BAR
            item {
                SearchBar(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // TITULO 1
            item {
                Text(
                    text = "Remodelaciones recomendadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
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
                val categories = listOf(
                    Pair(R.drawable.bano, "Baños"),
                    Pair(R.drawable.luz, "Iluminación"),
                    Pair(R.drawable.cocina, "Cocina"),
                    Pair(R.drawable.exterior, "Exterior")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(categories) { category ->
                        CategoryItem(
                            imageRes = category.first,
                            title = category.second
                        )
                    }
                }
            }

            // TITULO PUBLICACIONES
            item {
                SectionTitle(
                    text = "Publicaciones",
                    showArrow = true
                )
            }

            // PUBLICACIONES
            item {
                val publications = listOf(
                    Triple(R.drawable.sala, "Salas a tu medida", "Desde $300.000"),
                    Triple(R.drawable.comedor, "¡Arma tu comedor!", "Desde $450.000")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(publications) { publication ->
                        PublicationCard(
                            imageRes = publication.first,
                            title = publication.second,
                            price = publication.third,
                            onClick = onPublicationClick
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FixUpTheme(darkTheme = false) {
        FeedScreen()
    }
}
