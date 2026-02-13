package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightSnow)
            .padding(horizontal = 32.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back arrow
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = CharcoalBrown,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 8.dp)
                    .let { mod ->
                        mod
                    }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Registrarse",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pantalla en construcción",
            fontSize = 14.sp,
            color = GreyOlive
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button to feed
        Button(
            onClick = onContinueClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                    text = "Continuar al Feed",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back button
        Button(
            onClick = onBackClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreyOlive.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Volver al Login",
                color = CharcoalBrown,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    FixUpTheme(darkTheme = false) {
        RegisterScreen()
    }
}
