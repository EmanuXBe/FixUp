package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.*
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme


// pantalla de registro, solo recibe datos
@Composable
fun RegisterForm(
    email: String,
    onEmailChange: (String) -> Unit,
    cedula: String,
    onCedulaChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FixUpTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.email_placeholder),
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        FixUpTextField(
            value = cedula,
            onValueChange = onCedulaChange,
            placeholder = stringResource(R.string.cedula_placeholder),
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth()
        )

        FixUpTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password_placeholder),
            keyboardType = KeyboardType.Password,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        RoleSelector(
            selectedRole = selectedRole,
            onRoleSelected = onRoleSelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// pantalla principal con estado (padre) y se lo pasa al hijo

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    // estado del padre
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FixUpTitle(modifier = Modifier)

        Spacer(modifier = Modifier.height(20.dp))

        AuthTabs(
            isLoginSelected = false,
            onLoginClick = onBackClick,
            onRegisterClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // y pasamos el estado al hijo
        RegisterForm(
            email = email,
            onEmailChange = { email = it },
            cedula = cedula,
            onCedulaChange = { cedula = it },
            password = password,
            onPasswordChange = { password = it },
            selectedRole = selectedRole,
            onRoleSelected = { selectedRole = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        FixUpButton(
            text = stringResource(R.string.btn_register),
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons(modifier = Modifier.fillMaxWidth())

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