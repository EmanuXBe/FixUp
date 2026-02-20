package edu.javeriana.fixup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.ui.FeedScreen
import edu.javeriana.fixup.ui.LogInScreen
import edu.javeriana.fixup.ui.ProfileScreen
import edu.javeriana.fixup.ui.RegisterScreen
import edu.javeriana.fixup.ui.view.RentScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LogIn.route
    ) {
        // Login screen
        composable(AppScreens.LogIn.route) {
            LogInScreen(
                onContinueClick = {
                    navController.navigate(AppScreens.Feed.route)
                },
                onRegisterClick = {
                    navController.navigate(AppScreens.Register.route)
                }
            )
        }

        // Register placeholder screen
        composable(AppScreens.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                }
            )
        }

        // Feed screen
        composable(AppScreens.Feed.route) {
            FeedScreen(navController = navController)
        }

        // Rent screen
        composable(AppScreens.Rent.route) {
            RentScreen(navController = navController)
        }

        // Profile screen
        composable(AppScreens.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
