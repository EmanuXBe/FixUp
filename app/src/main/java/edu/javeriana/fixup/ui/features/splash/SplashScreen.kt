package edu.javeriana.fixup.ui.features.splash


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.javeriana.fixup.componentsUtils.FixUpTitle
import edu.javeriana.fixup.data.repository.AuthRepository

/**
 * Pantalla de splash que verifica si hay una sesión activa.
 * Si el usuario ya está autenticado → navega al Feed.
 * Si no hay sesión → navega al Login.
 */
@Composable
fun SplashScreen(
    onNavigateToFeed: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authRepository = AuthRepository()

    LaunchedEffect(Unit) {
        if (authRepository.isUserLoggedIn) {
            onNavigateToFeed()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        FixUpTitle()
    }
}