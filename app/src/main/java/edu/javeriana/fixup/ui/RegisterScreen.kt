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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.AuthDivider
import edu.javeriana.fixup.componentsUtils.SocialAuthButtons
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable //logo
fun FixUpLogo(
    modifier: Modifier = Modifier //buena practica
) {
    Text(
        text = stringResource(R.string.app_name_title),
        style = MaterialTheme.typography.displayLarge,
        color = GreyOlive,
        modifier = modifier
    )
}

@Composable //los tabs de inicio sesion el / y el registrarse
fun AuthNavigationTabs(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier //buena practica
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.tab_login),
            style = MaterialTheme.typography.titleMedium,
            color = CharcoalBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onLoginClick() }
        )
        Text(
            text = stringResource(R.string.tab_separator),
            style = MaterialTheme.typography.titleMedium,
            color = GreyOlive
        )
        Text(
            text = stringResource(R.string.tab_register),
            style = MaterialTheme.typography.titleMedium,
            color = SoftFawn,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable //el campo de texto de inicio de sesion y registro
fun FixUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderResId: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier //buena practica
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(placeholderResId),
                style = MaterialTheme.typography.bodyMedium,
                color = GreyOlive
            )
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SoftFawn,
            unfocusedBorderColor = GreyOlive.copy(alpha = 0.4f),
            cursorColor = SoftFawn,
            focusedTextColor = CharcoalBrown,
            unfocusedTextColor = CharcoalBrown
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}

@Composable //si es fixer o cliente
fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier //buena practica
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.role_label),
            style = MaterialTheme.typography.bodyMedium,
            color = CharcoalBrown,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            RoleButton(
                textResId = R.string.role_fixer,
                isSelected = selectedRole == "Fixer",
                onClick = { onRoleSelected("Fixer") },
                modifier = Modifier.weight(1f)
            )

            RoleButton(
                textResId = R.string.role_cliente,
                isSelected = selectedRole == "Cliente",
                onClick = { onRoleSelected("Cliente") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable // boton de seleccion de rol
fun RoleButton(
    textResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier //buena practica
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) SoftFawn else GreyOlive.copy(alpha = 0.3f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) SoftFawn.copy(alpha = 0.1f) else Color.Transparent
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(textResId),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) SoftFawn else CharcoalBrown,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable //boton de registrarse
fun GradientButton(
    textResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier //buena practica
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier.height(52.dp)
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
                text = stringResource(textResId),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@Composable //formulario de registro
fun RegisterForm(
    email: String,
    onEmailChange: (String) -> Unit,
    cedula: String,
    onCedulaChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier //buena practica
) {
    Column(modifier = modifier) {
        //email
        FixUpTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholderResId = R.string.email_placeholder,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        //cedula
        FixUpTextField(
            value = cedula,
            onValueChange = onCedulaChange,
            placeholderResId = R.string.cedula_placeholder,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        //contra
        FixUpTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholderResId = R.string.password_placeholder,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        //seleccionar
        RoleSelector(
            selectedRole = selectedRole,
            onRoleSelected = onRoleSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        //boton Registrarse
        GradientButton(
            textResId = R.string.btn_register,
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        FixUpLogo() //logo

        Spacer(modifier = Modifier.height(20.dp))

        AuthNavigationTabs( //navegacion
            onLoginClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        RegisterForm( // formulario
            email = email,
            onEmailChange = { email = it },
            cedula = cedula,
            onCedulaChange = { cedula = it },
            password = password,
            onPasswordChange = { password = it },
            selectedRole = selectedRole,
            onRoleSelected = { selectedRole = it },
            onRegisterClick = onContinueClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthDivider(modifier = Modifier.fillMaxWidth()) //divisor (componente reutilizable)

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons( //botones de autenticacion (componente reutilizable)
            googleTextResId = R.string.btn_google,
            appleTextResId = R.string.btn_apple,
            modifier = Modifier.fillMaxWidth()
        )

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