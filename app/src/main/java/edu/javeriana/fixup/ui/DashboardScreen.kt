package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun DashboardScreen() {

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            item {
                TopSection()
                Spacer(modifier = Modifier.height(20.dp))
                DashboardFilterChips()
                Spacer(modifier = Modifier.height(20.dp))
                StatsCards()
                Spacer(modifier = Modifier.height(20.dp))
                ChartSection()
                Spacer(modifier = Modifier.height(20.dp))
                RecentSection()
            }
        }
    }
}
@Composable
fun TopSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(Icons.Outlined.Menu, contentDescription = null)

        Text(
            text = "Fix&rent",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9C7C63)
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }
}
@Composable
fun DashboardFilterChips() {

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        AssistChip(
            onClick = { },
            label = { Text("Tus ganancias") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color.Black,
                labelColor = Color.White
            )
        )

        AssistChip(
            onClick = { },
            label = { Text("dinero por liberar") }
        )

        AssistChip(
            onClick = { },
            label = { Text("tu saldo") }
        )
    }
}
@Composable
fun StatsCards() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Ganancias")
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "$6.935.000",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "+20 % mas que el mes pasado",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("trabajos realizados")
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "10",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "+33 % que el mes pasado",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
@Composable
fun ChartSection() {

    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                "Tus ganancias mensuales",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE8F0FF), RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun RecentSection() {

    Card(shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "ultimas remodelaciones realizadas",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            RepeatUser("Elynn Lee", "email@fakedomain.net")
            Spacer(modifier = Modifier.height(12.dp))
            RepeatUser("Oscar Dum", "email@fakedomain.net")
        }
    }
}

@Composable
fun RepeatUser(name: String, email: String) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(name, fontWeight = FontWeight.Medium)
            Text(email, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    FixUpTheme {
        DashboardScreen()
    }
}