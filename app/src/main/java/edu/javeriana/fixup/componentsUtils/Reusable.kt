package edu.javeriana.fixup.componentsUtils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.Inter
import edu.javeriana.fixup.ui.theme.SoftFawn


@Composable
fun AuthDivider( //divisor de -o-
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = GreyOlive.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Text(
            text = stringResource(R.string.divider_or),
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
}


@Composable
fun OAuthButton( //autenticador de google o apple
    textResId: Int,
    icon: String,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, GreyOlive.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
        modifier = modifier.height(52.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (icon == "G") Inter else null,
            color = iconColor
        )
        Text(
            text = "  ${stringResource(textResId)}",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftFawn,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun SocialAuthButtons( //botones de autenticacion
    googleTextResId: Int,
    appleTextResId: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OAuthButton(
            textResId = googleTextResId,
            icon = "G",
            iconColor = SoftFawn,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OAuthButton(
            textResId = appleTextResId,
            icon = "\uF8FF",
            iconColor = CharcoalBrown,
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}