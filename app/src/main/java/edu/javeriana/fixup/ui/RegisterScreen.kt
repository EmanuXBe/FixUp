package edu.javeriana.fixup.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.Inter
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun RegisterScreen(
    onBackToLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") } // "Fixer" o "Cliente"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightSnow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // ── App Title: "FixUp" ──
        Text(
            text = "FixUp",
            style = MaterialTheme.typography.displayLarge,
            color = GreyOlive
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── "Iniciar Sesión / Registrarse" tabs ──
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleMedium,
                color = CharcoalBrown,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onBackToLoginClick() }
            )
            Text(
                text = " / ",
                style = MaterialTheme.typography.titleMedium,
                color = GreyOlive
            )
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.titleMedium,
                color = SoftFawn,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // ── Email Field ──
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    text = "email@domain.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyOlive
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftFawn,
                unfocusedBorderColor = GreyOlive.copy(alpha = 0.4f),
                cursorColor = SoftFawn,
                focusedTextColor = CharcoalBrown,
                unfocusedTextColor = CharcoalBrown
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Cédula Field ──
        OutlinedTextField(
            value = cedula,
            onValueChange = { cedula = it },
            placeholder = {
                Text(
                    text = "C.C.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyOlive
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftFawn,
                unfocusedBorderColor = GreyOlive.copy(alpha = 0.4f),
                cursorColor = SoftFawn,
                focusedTextColor = CharcoalBrown,
                unfocusedTextColor = CharcoalBrown
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Password Field ──
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyOlive
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SoftFawn,
                unfocusedBorderColor = GreyOlive.copy(alpha = 0.4f),
                cursorColor = SoftFawn,
                focusedTextColor = CharcoalBrown,
                unfocusedTextColor = CharcoalBrown
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Role Selection: Fixer / Cliente ──
        Text(
            text = "Selecciona tu rol:",
            style = MaterialTheme.typography.bodyMedium,
            color = CharcoalBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Fixer
            OutlinedButton(
                onClick = { selectedRole = "Fixer" },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    2.dp,
                    if (selectedRole == "Fixer") SoftFawn else GreyOlive.copy(alpha = 0.3f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedRole == "Fixer") SoftFawn.copy(alpha = 0.1f) else Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Fixer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedRole == "Fixer") SoftFawn else CharcoalBrown,
                    fontWeight = if (selectedRole == "Fixer") FontWeight.SemiBold else FontWeight.Normal
                )
            }

            // Botón Cliente
            OutlinedButton(
                onClick = { selectedRole = "Cliente" },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    2.dp,
                    if (selectedRole == "Cliente") SoftFawn else GreyOlive.copy(alpha = 0.3f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedRole == "Cliente") SoftFawn.copy(alpha = 0.1f) else Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Cliente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedRole == "Cliente") SoftFawn else CharcoalBrown,
                    fontWeight = if (selectedRole == "Cliente") FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Registrarse Button (gradient) ──
        Button(
            onClick = onRegisterClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = ButtonDefaults.ContentPadding,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SoftFawn, GreyOlive.copy(alpha = 0.7f))
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Divider with "o" ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = GreyOlive.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Text(
                text = "o",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = GreyOlive
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = GreyOlive.copy(alpha = 0.3f),
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Google Button ──
        OutlinedButton(
            onClick = { /* Not implemented */ },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, GreyOlive.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "G",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Inter,
                color = SoftFawn
            )
            Text(
                text = "  Registrarse con Google",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftFawn,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Apple Button ──
        OutlinedButton(
            onClick = { /* Not implemented */ },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, GreyOlive.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "\uF8FF",
                fontSize = 18.sp,
                color = CharcoalBrown
            )
            Text(
                text = "  Registrarse con Apple",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftFawn,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    FixUpTheme(darkTheme = false) {
        RegisterScreen()
    }
}