package edu.javeriana.fixup.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import edu.javeriana.fixup.ui.theme.FixUpTheme

data class RequestItem(
    val name: String,
    val time: String,
    val description: String,
    @DrawableRes val profileImage: Int,
    val hasPreview: Boolean = false,
    val showButton: Boolean = false,
    @DrawableRes val previewImage: Int? = null
)

@Composable
fun NewRequestsScreen() {

    val requests = listOf(
        RequestItem(
            name = "AndresContreras",
            time = "1d",
            description = "Ha solicitado una cotización",
            profileImage = R.drawable.pf1,
            showButton = true
        ),
        RequestItem(
            name = "nebulanomad",
            time = "1d",
            description = "Ha dado me gusta a tu idea",
            profileImage = R.drawable.pf2,
            hasPreview = true,
            previewImage = R.drawable.cocina
        ),
        RequestItem(
            name = "emberecho",
            time = "2d",
            description = "Ha escrito una review\nQuedó perfecto!!! 🎉🎉",
            profileImage = R.drawable.pf3
        ),
        RequestItem(
            name = "lunavoyager",
            time = "3d",
            description = "Ha guardado tu idea",
            profileImage = R.drawable.pf4,
            hasPreview = true,
            previewImage = R.drawable.comedor
        ),
        RequestItem(
            name = "shadowlynx",
            time = "4d",
            description = "Ha hecho un comentario en tu idea",
            profileImage = R.drawable.pf5,
            hasPreview = true,
            previewImage = R.drawable.exterior
        )
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tus nuevas solicitudes",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterChips()

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(requests) { request ->
                    RequestRow(request)
                }
            }
        }
    }
}

@Composable
fun FilterChips() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ChipItem("Recientes", selected = true)
        ChipItem("Antiguos")
        ChipItem("Propuestas")
        ChipItem("Otros")
    }
}

@Composable
fun ChipItem(text: String, selected: Boolean = false) {

    val backgroundColor = if (selected) Color.Black else Color(0xFFEDEDED)
    val textColor = if (selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(backgroundColor)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp
        )
    }
}

@Composable
fun RequestRow(item: RequestItem) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Punto rojo
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Avatar
        Image(
            painter = painterResource(id = item.profileImage),
            contentDescription = "Profile picture of ${item.name}",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = item.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        if (item.showButton) {
            Button(
                onClick = { /* sin funcionalidad */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Responder",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        if (item.hasPreview) {
            Spacer(modifier = Modifier.width(8.dp))

            item.previewImage?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewRequestsScreenPreview() {
    FixUpTheme {
        NewRequestsScreen()
    }
}