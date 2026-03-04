package edu.javeriana.fixup.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.javeriana.fixup.navigation.AppNavigation
import edu.javeriana.fixup.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definimos qué pantallas deben mostrar la barra superior y la de navegación
    val showBars = currentRoute in listOf(
        AppScreens.Feed.route,
        AppScreens.Rent.route,
        AppScreens.Notifications.route,
        AppScreens.Profile.route
    )

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = { Text("FixUp") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        },
        bottomBar = {
            if (showBars) {
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
