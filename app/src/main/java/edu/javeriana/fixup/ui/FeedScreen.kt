package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive

@Composable
fun FeedScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightSnow),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Feed",
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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FixUpTheme(darkTheme = false) {
        FeedScreen()
    }
}
