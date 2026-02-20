package edu.javeriana.fixup.componentsUtils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.Inter
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun FixUpTitle(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name_title),
        style = MaterialTheme.typography.displayLarge,
        color = GreyOlive,
        modifier = modifier
    )
}

@Composable
fun FixUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = GreyOlive
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SoftFawn,
            unfocusedBorderColor = GreyOlive.copy(alpha = 0.4f),
            cursorColor = SoftFawn,
            focusedTextColor = CharcoalBrown,
            unfocusedTextColor = CharcoalBrown
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun FixUpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SoftFawn,
            contentColor = Color.White,
            disabledContainerColor = SoftFawn.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AuthTabs(
    isLoginSelected: Boolean,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.tab_login),
            style = MaterialTheme.typography.titleMedium,
            color = if (isLoginSelected) SoftFawn else CharcoalBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(enabled = !isLoginSelected) { onLoginClick() }
        )
        Text(
            text = " ${stringResource(R.string.tab_separator)} ",
            style = MaterialTheme.typography.titleMedium,
            color = GreyOlive
        )
        Text(
            text = stringResource(R.string.tab_register),
            style = MaterialTheme.typography.titleMedium,
            color = if (!isLoginSelected) SoftFawn else CharcoalBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(enabled = isLoginSelected) { onRegisterClick() }
        )
    }
}

@Composable
fun AuthDivider(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
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
fun OAuthButton(
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
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Inter,
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
fun SocialAuthButtons(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OAuthButton(
            textResId = R.string.btn_google,
            icon = stringResource(R.string.google_icon),
            iconColor = SoftFawn,
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OAuthButton(
            textResId = R.string.btn_apple,
            icon = stringResource(R.string.apple_icon),
            iconColor = CharcoalBrown,
            onClick = { /* TODO */ }
        )
    }
}
