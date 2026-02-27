package edu.javeriana.fixup.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.javeriana.fixup.ui.FeedScreen
import edu.javeriana.fixup.ui.LogInScreen
import edu.javeriana.fixup.ui.ProfileScreen
import edu.javeriana.fixup.ui.PropertyDetailScreen
import edu.javeriana.fixup.ui.PublicationScreen
import edu.javeriana.fixup.ui.RegisterScreen
import edu.javeriana.fixup.ui.RentScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

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

        // Feed placeholder screen
        composable(AppScreens.Feed.route) {
            FeedScreen(
                onHomeClick = { /* Ya estamos en home */ },
                onSearchClick = { navController.navigate(AppScreens.Rent.route) },
                onProfileClick = { navController.navigate(AppScreens.Profile.route) },
                onPublicationClick = { navController.navigate(AppScreens.Publication.route) }
            )
        }

        composable(AppScreens.Rent.route) {
            RentScreen(
                onSelectClick = { navController.navigate(AppScreens.PropertyDetail.route) },
                onHomeClick = { navController.navigate(AppScreens.Feed.route) },
                onSearchClick = { /* Ya estamos en rent */ },
                onProfileClick = { navController.navigate(AppScreens.Profile.route) }
            )
        }

        composable(AppScreens.PropertyDetail.route) {
            PropertyDetailScreen(onBackClick = { navController.popBackStack() })
        }

        // Profile screen
        composable(AppScreens.Profile.route) {
            val sharedPrefs = context.getSharedPreferences("fixup_prefs", Context.MODE_PRIVATE)
            ProfileScreen(
                sp = sharedPrefs,
                onHomeClick = { navController.navigate(AppScreens.Feed.route) },
                onSearchClick = { navController.navigate(AppScreens.Rent.route) },
                onProfileClick = { /* Ya estamos en perfil */ }
            )
        }

        // Publication screen
        composable(AppScreens.Publication.route) {
            PublicationScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
