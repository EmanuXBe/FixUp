package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Ingresa tu nueva idea aqui") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Barra de navegacion")
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
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
                Image(
                    painter = painterResource(R.drawable.featured_image),
                    contentDescription = "imagen destacada de la semana",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            //TITULO CATEGORIAS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Categorias",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            Image(
                                painter = painterResource(category.first),
                                contentDescription = "barra de categorias",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = category.second)
                        }
                    }
                }
            }

            //TITULO PUBLICACIONES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Publicaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.KeyboardArrowRight, null)
                }
            }

            //PUBLICACIONES
            item {

                val publications = listOf(
                    Triple(R.drawable.sala, "Salas a tu medida", "Desde $300.000"),
                    Triple(R.drawable.comedor, "¡Arma tu comedor!", "Desde $450.000")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(publications) { publication ->

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.width(220.dp)
                        ) {

                            Column {

                                Image(
                                    painter = painterResource(publication.first),
                                    contentDescription = "Publicaciones",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                )

                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text(
                                        text = publication.second,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = publication.third,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

//BOTTOM NAV
@Composable
fun BottomNavBar() {

    NavigationBar {

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.Home, null) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.List, null) }
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