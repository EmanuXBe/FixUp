package edu.javeriana.fixup.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.javeriana.fixup.navigation.AppScreens
import edu.javeriana.fixup.ui.theme.SoftFawn

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White, // Fondo blanco para que haga juego con la app
        contentColor = SoftFawn
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Inicio") },
            selected = currentRoute == AppScreens.Feed.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SoftFawn,
                selectedTextColor = SoftFawn,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF5F5F5) // Un gris muy tenue para el círculo de selección
            ),
            onClick = {
                navController.navigate(AppScreens.Feed.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Rent") },
            label = { Text("Alquilar") },
            selected = currentRoute == AppScreens.Rent.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SoftFawn,
                selectedTextColor = SoftFawn,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF5F5F5)
            ),
            onClick = {
                navController.navigate(AppScreens.Rent.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Notifications, contentDescription = "Notifications") },
            label = { Text("Alertas") },
            selected = currentRoute == AppScreens.Notifications.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SoftFawn,
                selectedTextColor = SoftFawn,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF5F5F5)
            ),
            onClick = {
                navController.navigate(AppScreens.Notifications.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Perfil") },
            selected = currentRoute == AppScreens.Profile.route,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SoftFawn,
                selectedTextColor = SoftFawn,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF5F5F5)
            ),
            onClick = {
                navController.navigate(AppScreens.Profile.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
