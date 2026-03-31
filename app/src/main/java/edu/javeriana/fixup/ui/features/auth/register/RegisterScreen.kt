package edu.javeriana.fixup.ui.features.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val cedulaFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { cedulaFocusRequester.requestFocus() }
                ),
                isError = uiState.error != null,
                modifier = Modifier.fillMaxWidth()
            )

            FixUpTextField(
                value = uiState.cedula,
                onValueChange = { viewModel.onCedulaChanged(it) },
                placeholder = stringResource(R.string.cedula_placeholder),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(cedulaFocusRequester)
            )

            FixUpTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = stringResource(R.string.password_placeholder),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.signUp(onSuccess = onRegisterSuccess)
                    }
                ),
                isPassword = true,
                isError = uiState.error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
            )

            RoleSelector(
                selectedRole = uiState.selectedRole,
                onRoleSelected = { viewModel.onRoleSelected(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mensaje de error
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color(0xFFBFA980))
        } else {
            FixUpButton(
                text = stringResource(R.string.btn_register),
                onClick = { viewModel.signUp(onSuccess = onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth()
            )
        }

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
    FixUpTheme {
        RegisterScreen()
    }
}