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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.Comfortaa
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.Inter
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun LogInScreen(
    onContinueClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightSnow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // ── App Title: "FixUp" in Comfortaa 48sp ──
        Text(
            text = "FixUp",
            style = MaterialTheme.typography.displayLarge,
            color = GreyOlive
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Terms of Service: Inter 12sp ──
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = GreyOlive)) {
                    append("Al hacer clic en continuar, aceptas nuestros ")
                }
                withStyle(style = SpanStyle(color = CharcoalBrown, fontWeight = FontWeight.SemiBold)) {
                    append("Términos de Servicio")
                }
                withStyle(style = SpanStyle(color = GreyOlive)) {
                    append(" y nuestra ")
                }
                withStyle(style = SpanStyle(color = CharcoalBrown, fontWeight = FontWeight.SemiBold)) {
                    append("Política de Privacidad")
                }
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── "Iniciar Sesion / Registrarse" tabs: Comfortaa 16sp ──
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Iniciar Sesion",
                style = MaterialTheme.typography.titleMedium,
                color = SoftFawn,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = " / ",
                style = MaterialTheme.typography.titleMedium,
                color = GreyOlive
            )
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.titleMedium,
                color = CharcoalBrown,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onRegisterClick() }
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

        Spacer(modifier = Modifier.height(24.dp))

        // ── Continuar Button (gradient) ──
        Button(
            onClick = onContinueClick,
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
                    text = "Continuar",
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
                text = "  Continuar con Google",
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
                text = "  Continuar con Apple",
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
fun LogInScreenPreview() {
    FixUpTheme(darkTheme = false) {
        LogInScreen()
    }
}