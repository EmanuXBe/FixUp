package edu.javeriana.fixup.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.javeriana.fixup.R
import edu.javeriana.fixup.componentsUtils.AuthDivider
import edu.javeriana.fixup.componentsUtils.AuthTabs
import edu.javeriana.fixup.componentsUtils.FixUpButton
import edu.javeriana.fixup.componentsUtils.FixUpTextField
import edu.javeriana.fixup.componentsUtils.FixUpTitle
import edu.javeriana.fixup.componentsUtils.SocialAuthButtons
import edu.javeriana.fixup.ui.theme.BrightSnow
import edu.javeriana.fixup.ui.theme.CharcoalBrown
import edu.javeriana.fixup.ui.theme.FixUpTheme
import edu.javeriana.fixup.ui.theme.GreyOlive
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
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
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FixUpTitle()

        Spacer(modifier = Modifier.height(20.dp))

        AuthTabs(
            isLoginSelected = false,
            onLoginClick = onBackClick,
            onRegisterClick = {}
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
            value = cedula,
            onValueChange = { cedula = it },
            placeholder = stringResource(R.string.cedula_placeholder),
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(12.dp))

        FixUpTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.password_placeholder),
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoleSelector(
            selectedRole = selectedRole,
            onRoleSelected = { selectedRole = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FixUpButton(
            text = stringResource(R.string.btn_register),
            onClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(28.dp))

        SocialAuthButtons(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
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
                text = stringResource(R.string.role_fixer),
                isSelected = selectedRole == "Fixer",
                onClick = { onRoleSelected("Fixer") },
                modifier = Modifier.weight(1f)
            )

            RoleButton(
                text = stringResource(R.string.role_cliente),
                isSelected = selectedRole == "Cliente",
                onClick = { onRoleSelected("Cliente") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RoleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) SoftFawn else CharcoalBrown,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    FixUpTheme(darkTheme = false) {
        RegisterScreen()
    }
}
