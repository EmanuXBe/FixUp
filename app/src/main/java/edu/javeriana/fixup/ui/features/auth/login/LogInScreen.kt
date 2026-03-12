package edu.javeriana.fixup.ui.features.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun LogInScreen(
    modifier: Modifier = Modifier,
    viewModel: LogInViewModel = viewModel(),
    onContinueClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
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
        LoginHeader(onRegisterClick = onRegisterClick)

        Spacer(modifier = Modifier.height(48.dp))

        LoginForm(
            email = uiState.email,
            password = uiState.password,
            onEmailChange = { viewModel.onEmailChanged(it) },
            onPasswordChange = { viewModel.onPasswordChanged(it) },
            onContinueClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        LoginFooter()
    }
}

@Composable
private fun LoginHeader(onRegisterClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FixUpTitle()

        Spacer(modifier = Modifier.height(20.dp))

        TermsOfServiceText()

        Spacer(modifier = Modifier.height(28.dp))

        AuthTabs(
            isLoginSelected = true,
            onLoginClick = {},
            onRegisterClick = onRegisterClick
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FixUpTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.email_placeholder),
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        FixUpTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password_placeholder),
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        FixUpButton(
            text = stringResource(R.string.btn_continue),
            onClick = onContinueClick
        )
    }
}

@Composable
private fun LoginFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AuthDivider()

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LogInScreenPreview() {
    FixUpTheme {
        LogInScreen()
    }
}
