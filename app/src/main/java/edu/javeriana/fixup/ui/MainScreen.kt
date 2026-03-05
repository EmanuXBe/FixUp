package edu.javeriana.fixup.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.navigation.AppNavigation
import edu.javeriana.fixup.navigation.AppScreens
import edu.javeriana.fixup.ui.theme.FixUpTheme

@Composable
fun MainScreen(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definimos qué pantallas deben mostrar la barra de navegación
    val showBottomBar = currentRoute in listOf(
        AppScreens.Feed.route,
        AppScreens.Rent.route,
        AppScreens.Notifications.route,
        AppScreens.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FixUpTheme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
