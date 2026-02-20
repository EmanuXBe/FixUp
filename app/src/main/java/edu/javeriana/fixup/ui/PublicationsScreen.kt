package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.SearchBar
import edu.javeriana.fixup.ui.model.PublicationUiModel
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme


@Composable
fun PublicationGridCard(
    publication: PublicationUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(publication.imageRes),
                contentDescription = publication.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = publication.category,
                    fontSize = 12.sp,
                    color = Color(0x80000000)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = publication.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedString {
                        val prefix = "Desde "
                        if (publication.price.startsWith(prefix, ignoreCase = true)) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(prefix)
                            }
                            append(publication.price.substring(prefix.length))
                        } else {
                            append(publication.price)
                        }
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun SectionTitleSimple(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}


@Composable
fun PublicationsBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Default.Home, null) }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Default.ShoppingCart, null) }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            icon = { Icon(Icons.Default.Notifications, null) }
        )

        NavigationBarItem(
            selected = selectedIndex == 4,
            onClick = { onItemSelected(4) },
            icon = { Icon(Icons.Default.Person, null) }
        )
    }
}

@Composable
fun PublicationsScreen(
    modifier: Modifier = Modifier
) {

    var searchQuery by remember { mutableStateOf("") }
    var selectedNavItem by remember { mutableStateOf(1) }

    // 🔹 Datos quemados usando entidad de UI
    val publications = listOf(
        PublicationUiModel(R.drawable.sala, "Salas", "Salas a tu medida", "Desde $300.000"),
        PublicationUiModel(R.drawable.comedor, "Comedores", "¡Arma tu comedor!", "Desde $450.000"),
        PublicationUiModel(R.drawable.pisos, "Pisos y paredes", "Cambia pisos y paredes", "Desde $150.000"),
        PublicationUiModel(R.drawable.pisos2, "Pisos y paredes", "Remodela tu piso", "Desde $90.000"),
        PublicationUiModel(R.drawable.luz, "Iluminación", "Ilumina tu hogar", "Desde $200.000"),
        PublicationUiModel(R.drawable.cocina, "Cocina", "Renueva tu cocina", "Desde $500.000")
    )

    val filteredPublications = publications.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BrightSnow,
        bottomBar = {
            PublicationsBottomNavBar(
                selectedIndex = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it }
            )

            SectionTitleSimple(
                text = "Publicaciones"
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPublications) { publication ->
                    PublicationGridCard(
                        publication = publication
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PublicationsScreenPreview() {
    FixUpTheme(darkTheme = false) {
        PublicationsScreen()
    }
}