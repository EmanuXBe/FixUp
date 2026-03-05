package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.*
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FixUpTitle()

        Spacer(modifier = Modifier.height(20.dp))

        AuthTabs(
            isLoginSelected = false,
            onLoginClick = onBackClick,
            onRegisterClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FixUpTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = stringResource(R.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            FixUpTextField(
                value = uiState.cedula,
                onValueChange = { viewModel.onCedulaChanged(it) },
                placeholder = stringResource(R.string.cedula_placeholder),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )

            FixUpTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = stringResource(R.string.password_placeholder),
                keyboardType = KeyboardType.Password,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            RoleSelector(
                selectedRole = uiState.selectedRole,
                onRoleSelected = { viewModel.onRoleSelected(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

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
