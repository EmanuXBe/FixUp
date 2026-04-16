package edu.javeriana.fixup.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.ui.theme.SoftFawn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProfileDialog(
            initialName = uiState.name,
            initialEmail = uiState.email,
            initialPhone = uiState.phone,
            initialAddress = uiState.address,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, email, phone, address ->
                viewModel.updateProfileInfo(name, email, phone, address)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", color = SoftFawn) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = SoftFawn
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar Perfil", tint = SoftFawn)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Información Personal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftFawn
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsInfoRow(icon = Icons.Outlined.Person, value = uiState.name)
                    SettingsInfoRow(icon = Icons.Outlined.LocationOn, value = uiState.address)
                    SettingsInfoRow(icon = Icons.Outlined.Phone, value = uiState.phone)
                    SettingsInfoRow(icon = Icons.Outlined.Email, value = uiState.email)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gestión de Cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftFawn
                )
            }

            // ── Botones de acción 2x2 ───────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Home,
                            text = "Mis casas"
                        )
                        SettingsActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CreditCard,
                            text = "Pagos"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.History,
                            text = "Historial"
                        )
                        SettingsActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Refresh,
                            text = "Pedidos"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // ── Botón de Cerrar Sesión ──────────────────────────────
                Button(
                    onClick = {
                        viewModel.signOut()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterStart)
                        )
                        Text(
                            text = "Cerrar sesión",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialEmail: String,
    initialPhone: String,
    initialAddress: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil", color = SoftFawn) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, email, phone, address) }) {
                Text("Guardar", color = SoftFawn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SettingsInfoRow(icon: ImageVector, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
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
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SettingsActionButton(modifier: Modifier = Modifier, icon: ImageVector, text: String) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
