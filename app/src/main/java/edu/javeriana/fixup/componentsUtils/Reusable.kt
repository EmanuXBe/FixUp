package edu.javeriana.fixup.componentsUtils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.Inter
import edu.javeriana.fixup.ui.theme.SoftFawn
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.fillMaxWidth

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
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,  // ← AGREGAR ESTA LÍNEA
    isError: Boolean = false,      // ← AGREGAR ESTA LÍNEA
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,  // ← USAR isPassword AQUÍ
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) Color.Red else SoftFawn,
            unfocusedBorderColor = if (isError) Color.Red.copy(alpha = 0.4f) else GreyOlive.copy(alpha = 0.4f),
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

@Composable
fun TermsOfServiceText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.terms_of_service),
        style = MaterialTheme.typography.bodySmall,
        color = GreyOlive,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(R.string.search_placeholder), color = GreyOlive) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreyOlive) },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SoftFawn,
            unfocusedBorderColor = GreyOlive.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = CharcoalBrown
        )
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SoftFawn,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FeaturedImage(imageRes: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun CategoryItem(imageRes: Int, title: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SoftFawn.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = CharcoalBrown,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PublicationCard(imageRes: Int, title: String, price: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.width(200.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftFawn,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
            label = { Text(stringResource(R.string.nav_home)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.nav_search)) },
            label = { Text(stringResource(R.string.nav_search)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.nav_profile)) },
            label = { Text(stringResource(R.string.nav_profile)) }
        )
    }
}

@Composable
fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val roles = listOf("Cliente", "Especialista")
        roles.forEach { role ->
            val isSelected = selectedRole == role
            OutlinedButton(
                onClick = { onRoleSelected(role) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) SoftFawn else GreyOlive.copy(alpha = 0.3f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) SoftFawn.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Text(
                    text = role,
                    color = if (isSelected) SoftFawn else CharcoalBrown,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
