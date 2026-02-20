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
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable //tarjeta de publicacion
fun PublicationGridCard(
    imageRes: Int,
    category: String,
    title: String,
    price: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp), // Esquinas redondeadas
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Imagen superior de la tarjeta
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop, //ajusta la imagen recortándola si es necesario
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            )

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                // Texto de categoría
                Text(
                    text = category,
                    fontSize = 12.sp,
                    color = Color(0x80000000)
                )

                Spacer(modifier = Modifier.height(4.dp))

                //titulo del producto
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2, // Máximo 2 líneas
                    overflow = TextOverflow.Ellipsis // Si es largo, agrega "..."
                )

                Spacer(modifier = Modifier.height(4.dp))

                // precio
                Text(
                    text = buildAnnotatedString {
                        val prefix = "Desde "
                        if (price.startsWith(prefix, ignoreCase = true)) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(prefix)
                            }
                            append(price.substring(prefix.length))
                        } else {
                            append(price)
                        }
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable //titulo de seccion
fun SectionTitleSimple(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // Separa texto e icono
    ) {
        // Texto del título
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Icono de flecha
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Composable //boton de navigation bar
fun PublicationsBottomNavBar(
    selectedIndex: Int, // Índice seleccionado actualmente
    onItemSelected: (Int) -> Unit, // Función que se ejecuta al seleccionar un item
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        // Item 0 - Home
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Default.Home, null) }
        )

        // Item 1 - Lista
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) }
        )

        // Item 2 - Carrito
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Default.ShoppingCart, null) }
        )

        // Item 3 - Notificaciones
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            icon = { Icon(Icons.Default.Notifications, null) }
        )

        // Item 4 - Perfil
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
    // Guarda el texto que el usuario escribe en el buscador
    var searchQuery by remember { mutableStateOf("") }

    // Guarda qué item del bottom nav está seleccionado
    var selectedNavItem by remember { mutableStateOf(1) }

    // Lista estática de publicaciones
    val publications = listOf(
        listOf(R.drawable.sala, "Salas", "Salas a tu medida", "Desde $300.000"),
        listOf(R.drawable.comedor, "Comedores", "¡Arma tu comedor!", "Desde $450.000"),
        listOf(R.drawable.pisos, "Pisos y paredes", "Cambia pisos y paredes", "Desde $150.000"),
        listOf(R.drawable.pisos2, "Pisos y paredes", "Remodela tu piso", "Desde $90.000"),
        listOf(R.drawable.luz, "Iluminación", "Ilumina tu hogar", "Desde $200.000"),
        listOf(R.drawable.cocina, "Cocina", "Renueva tu cocina", "Desde $500.000")
    )

    //filtro de busqueda
    val filteredPublications = publications.filter {
        (it[2] as String).contains(searchQuery, ignoreCase = true) ||
                (it[1] as String).contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BrightSnow, // Color de fondo
        bottomBar = {
            // Barra inferior
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

            // Barra de búsqueda (se actualiza el estado)
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
            )

            // Título de sección
            SectionTitleSimple(
                text = "Publicaciones"
            )

            // Grid de publicaciones (2 columnas)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Por cada publicación filtrada se crea una tarjeta
                items(filteredPublications) { publication ->
                    PublicationGridCard(
                        imageRes = publication[0] as Int,
                        category = publication[1] as String,
                        title = publication[2] as String,
                        price = publication[3] as String
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
