package edu.javeriana.fixup.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.javeriana.fixup.ui.*

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
            FeedScreen(
                onHomeClick = { /* Ya estamos en home */ },
                onSearchClick = { navController.navigate(AppScreens.Rent.route) },
                onNotificationsClick = { navController.navigate(AppScreens.Notifications.route) },
                onProfileClick = { navController.navigate(AppScreens.Profile.route) },
                onPublicationClick = { navController.navigate(AppScreens.Publication.route) }
            )
        }

        // Rent screen
        composable(AppScreens.Rent.route) {
            RentScreen(
                onSelectClick = { id -> 
                    navController.navigate(AppScreens.PropertyDetail.route + "/$id") 
                },
                onHomeClick = { navController.navigate(AppScreens.Feed.route) },
                onSearchClick = { /* Ya estamos en rent */ },
                onNotificationsClick = { navController.navigate(AppScreens.Notifications.route) },
                onProfileClick = { navController.navigate(AppScreens.Profile.route) }
            )
        }

        // Notifications screen
        composable(AppScreens.Notifications.route) {
            NewRequestsScreen(
                onHomeClick = { navController.navigate(AppScreens.Feed.route) },
                onSearchClick = { navController.navigate(AppScreens.Rent.route) },
                onNotificationsClick = { /* Ya estamos en notificaciones */ },
                onProfileClick = { navController.navigate(AppScreens.Profile.route) }
            )
        }

        // Profile screen
        composable(AppScreens.Profile.route) {
            ProfileScreen(
                onHomeClick = { navController.navigate(AppScreens.Feed.route) },
                onSearchClick = { navController.navigate(AppScreens.Rent.route) },
                onNotificationsClick = { navController.navigate(AppScreens.Notifications.route) },
                onProfileClick = { /* Ya estamos en perfil */ }
            )
        }

        composable(
            route = AppScreens.PropertyDetail.route + "/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            PropertyDetailScreen(
                propertyId = propertyId,
                onBackClick = { navController.popBackStack() },
                onReserveClick = { navController.navigate(AppScreens.Checkout.route) }
            )
        }

        // Publication screen
        composable(AppScreens.Publication.route) {
            PublicationScreen(
                onBackClick = { navController.popBackStack() },
                onContactClick = { navController.navigate(AppScreens.Checkout.route) }
            )
        }

        // Checkout screen
        composable(AppScreens.Checkout.route) {
            CheckoutScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
