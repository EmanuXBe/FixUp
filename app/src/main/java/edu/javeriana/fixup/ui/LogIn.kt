package edu.javeriana.fixup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        TermsOfServiceText()

        Spacer(modifier = Modifier.height(28.dp))

        AuthTabs(
            isLoginSelected = true,
            onLoginClick = {},
            onRegisterClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(48.dp))

        FixUpTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.email_placeholder),
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        FixUpTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.password_placeholder),
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        FixUpButton(
            text = stringResource(R.string.btn_continue),
            onClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LogInScreenPreview() {
    FixUpTheme(darkTheme = false) {
        LogInScreen()
    }
}
