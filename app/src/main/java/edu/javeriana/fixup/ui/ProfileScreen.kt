package edu.javeriana.fixup.ui

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.BottomNavBar
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun ProfileScreen(
    sp: SharedPreferences,
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val name = sp.getString("name", "Gabo peñuela") ?: ""
    val address = sp.getString("address", "Calle 1 # 1-99 conjunto Alegre") ?: ""
    val phone = sp.getString("phone", "3002001010") ?: ""
    val email = sp.getString("email", "jhondoe@siemprealegre.com") ?: ""

    Scaffold(
        bottomBar = {
            BottomNavBar(
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick,
                currentScreen = "profile"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Status Bar ──────────────────────────────────────────
            StatusBar()

            // Espacio extra para bajar el contenido
            Spacer(modifier = Modifier.height(60.dp))

            // ── Foto de perfil ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_photo),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Normal,
                color = SoftFawn,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cliente estrella",
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Campos de información ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(icon = Icons.Outlined.LocationOn, value = address)
                InfoRow(icon = Icons.Outlined.Phone, value = phone)
                InfoRow(icon = Icons.Outlined.Email, value = email)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botones de acción 2x2 ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Home,
                        text = "Mis casas guardadas"
                    )
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.CreditCard,
                        text = "Pagos"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Settings,
                        text = "Ajustes"
                    )
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Refresh,
                        text = "Tus remodelaciones"
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.SignalCellularAlt,
                contentDescription = "Señal",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Icon(
                imageVector = Icons.Outlined.Wifi,
                contentDescription = "Wifi",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Icon(
                imageVector = Icons.Outlined.BatteryFull,
                contentDescription = "Batería",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SoftFawn,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color(0xFF555555),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "Editar",
            tint = SoftFawn,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ActionButton(modifier: Modifier = Modifier, icon: ImageVector, text: String) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF555555)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF888888),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF555555),
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreenContent(
        name = "Gabo peñuela",
        address = "Calle 1 # 1-99 conjunto Alegre",
        phone = "3002001010",
        email = "jhondoe@siemprealegre.com"
    )
}

@Composable
fun ProfileScreenContent(name: String, address: String, phone: String, email: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Status Bar ──────────────────────────────────────────
        StatusBar()

        // Espacio extra para bajar el contenido
        Spacer(modifier = Modifier.height(60.dp))

        // ── Foto de perfil ──────────────────────────────────────
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_photo),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Normal,
            color = SoftFawn,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Cliente estrella",
            fontSize = 14.sp,
            color = Color(0xFF888888)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // ── Campos de información ───────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(icon = Icons.Outlined.LocationOn, value = address)
            InfoRow(icon = Icons.Outlined.Phone, value = phone)
            InfoRow(icon = Icons.Outlined.Email, value = email)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Botones de acción 2x2 ───────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Home,
                    text = "Mis casas guardadas"
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CreditCard,
                    text = "Pagos"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Settings,
                    text = "Ajustes"
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Refresh,
                    text = "Tus remodelaciones"
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
