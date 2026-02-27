package edu.javeriana.fixup.ui

import edu.javeriana.fixup.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.javeriana.fixup.ui.theme.FixUpTheme

data class CheckoutItemUiModel(
    val imageRes: Int,
    val category: String,
    val title: String,
    val description: String,
    val price: String
)

@Composable
fun CheckoutScreen(
    modifier: Modifier = Modifier
) {

    var selectedAddress by remember { mutableStateOf("Direcciones guardadas") }
    var selectedDate by remember { mutableStateOf("5 de octubre 2025") }
    var selectedPayment by remember { mutableStateOf("Visa *1234") }

    val items = listOf(
        CheckoutItemUiModel(
            imageRes = R.drawable.cocina,
            category = "Iluminacion",
            title = "Luces para entrada",
            description = "Instalacion incluida",
            price = "$350.000"
        ),
        CheckoutItemUiModel(
            imageRes = R.drawable.comedor,
            category = "Lavanderia",
            title = "Crea tu zona de lavado",
            description = "Materiales cotizados",
            price = "$1.550.000"
        )
    )

    val subtotal = "$1.900.000"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        CheckoutHeader()

        CheckoutOptionRow(
            label = "Direccion",
            value = selectedAddress,
            onClick = { }
        )

        CheckoutOptionRow(
            label = "Dia",
            value = "Programado\n$selectedDate",
            onClick = { }
        )

        CheckoutOptionRow(
            label = "PAGO",
            value = selectedPayment,
            onClick = { }
        )

        CheckoutOptionRow(
            label = "PROMOCIONES",
            value = "Aplicar código promocional",
            onClick = { }
        )

        CheckoutItemsSection(items = items)

        CheckoutSummary(subtotal = subtotal)

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Haz un pedido")
        }
    }
}

@Composable
fun CheckoutHeader(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = "Pantalla de pago",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CheckoutOptionRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = value,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun CheckoutItemsSection(
    items: List<CheckoutItemUiModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(
                text = "Tus ideas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(60.dp))
                Text(
                    text = "Precio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items.forEach { item ->
            CheckoutItemCard(item = item)
        }
    }
}

@Composable
fun CheckoutItemCard(
    item: CheckoutItemUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(item.imageRes),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(item.category, style = MaterialTheme.typography.labelMedium)
            Text(item.title, style = MaterialTheme.typography.bodyLarge)
            Text(item.description, style = MaterialTheme.typography.bodySmall)
        }

        Text(
            text = item.price,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutSummary(
    subtotal: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        SummaryRow(label = "Subtotal (2)", value = subtotal)
        SummaryRow(label = "Costos extra", value = "Gratis")
        SummaryRow(label = "Total", value = subtotal, isBold = true)
    }
}

@Composable
fun SummaryRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    FixUpTheme {
        CheckoutScreen()
    }
}
