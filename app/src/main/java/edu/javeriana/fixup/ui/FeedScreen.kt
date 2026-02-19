package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Ingresa tu nueva idea aqui") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Barra de navegacion")
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false,
    autoMirrored: Boolean = false
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
        if (showArrow) {
            if (autoMirrored) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
            }
        }
    }
}

@Composable
fun FeaturedImage(
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = "imagen destacada de la semana",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(18.dp))
    )
}

@Composable
fun CategoryItem(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "barra de categorias",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title)
    }
}

@Composable
fun PublicationCard(
    imageRes: Int,
    title: String,
    price: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.width(220.dp)
    ) {
        Column {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Publicaciones",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FeedScreen() {

    Scaffold(
        containerColor = BrightSnow,
        bottomBar = { BottomNavBar() }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            //SEARCH BAR
            item {
                SearchBar(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                )
            }

            //TITULO 1
            item {
                Text(
                    text = "Remodelaciones recomendadas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            //IMAGEN DESTACADA
            item {
                FeaturedImage(
                    imageRes = R.drawable.featured_image,
                    modifier = Modifier
                )
            }

            //TITULO CATEGORIAS
            item {
                SectionTitle(
                    text = "Categorias",
                    showArrow = true,
                    autoMirrored = true,
                    modifier = Modifier
                )
            }

            //CATEGORIAS
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

            //TITULO PUBLICACIONES
            item {
                SectionTitle(
                    text = "Publicaciones",
                    showArrow = true,
                    autoMirrored = false,
                    modifier = Modifier
                )
            }

            //PUBLICACIONES
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
                            price = publication.third
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

//BOTTOM NAV
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier
    ) {

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, null) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.ShoppingCart, null) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Notifications, null) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Person, null) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FixUpTheme(darkTheme = false) {
        FeedScreen()
    }
}