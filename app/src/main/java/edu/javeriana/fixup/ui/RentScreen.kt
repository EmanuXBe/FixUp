package edu.javeriana.fixup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.BottomNavBar
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun RentScreen(
    onSelectClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick,
                currentScreen = "search"
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SearchSection()
                Spacer(modifier = Modifier.height(12.dp))
                FilterSection()
                Spacer(modifier = Modifier.height(12.dp))
                MapSection()
                Spacer(modifier = Modifier.height(16.dp))
                PropertyCard(onSelectClick = onSelectClick)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SearchSection() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF0F0F0),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Arriendos", fontWeight = FontWeight.SemiBold)
                Text(
                    "3 habitaciones · 2 baños · parqueadero",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FilterSection() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            AssistChip(
                onClick = {},
                label = { Text("Filtro") }
            )

            AssistChip(
                onClick = {},
                label = { Text("Clasificar") }
            )
        }

        Text(
            "99 resultados",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MapSection() {

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE6E6E6),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = "Mapa de la ubicación",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PropertyCard(onSelectClick: () -> Unit) {

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column {

            Image(
                painter = painterResource(id = R.drawable.chapi),
                contentDescription = "Foto del inmueble",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Apartamento en chapinero",
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    "Remodelado con nosotros",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "3 habitaciones",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    "2 baños",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "1.250.000 $ /Mes",
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = onSelectClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text("Seleccionar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RentScreenPreview() {
    FixUpTheme {
        RentScreen(onSelectClick = {})
    }
}
