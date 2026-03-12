package edu.javeriana.fixup.ui.features.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import edu.javeriana.fixup.ui.theme.FixUpTheme

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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RegisterHeader(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(48.dp))

        RegisterForm(
            uiState = uiState,
            viewModel = viewModel,
            onContinueClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        RegisterFooter()
    }
}

@Composable
private fun RegisterHeader(onBackClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FixUpTitle()

        Spacer(modifier = Modifier.height(20.dp))

        AuthTabs(
            isLoginSelected = false,
            onLoginClick = onBackClick,
            onRegisterClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RegisterForm(
    uiState: RegisterUiState,
    viewModel: RegisterViewModel,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FixUpTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            placeholder = stringResource(R.string.email_placeholder),
            keyboardType = KeyboardType.Email
        )

        FixUpTextField(
            value = uiState.cedula,
            onValueChange = { viewModel.onCedulaChanged(it) },
            placeholder = stringResource(R.string.cedula_placeholder),
            keyboardType = KeyboardType.Number
        )

        FixUpTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            placeholder = stringResource(R.string.password_placeholder),
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        RoleSelector(
            selectedRole = uiState.selectedRole,
            onRoleSelected = { viewModel.onRoleSelected(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FixUpButton(
            text = stringResource(R.string.btn_register),
            onClick = onContinueClick
        )
    }
}

@Composable
private fun RegisterFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AuthDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    FixUpTheme {
        RegisterScreen()
    }
}
