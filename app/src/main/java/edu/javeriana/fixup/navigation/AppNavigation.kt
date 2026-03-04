package edu.javeriana.fixup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.javeriana.fixup.ui.*

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.LogIn.route,
        modifier = modifier
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
        
        // Register screen
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
                onPublicationClick = { 
                    navController.navigate(AppScreens.Publication.route) 
                }
            )
        }

        // Rent screen
        composable(AppScreens.Rent.route) {
            RentScreen(
                onSelectClick = { id -> 
                    navController.navigate(AppScreens.PropertyDetail.route + "/$id") 
                }
            )
        }

        // Notifications screen
        composable(AppScreens.Notifications.route) {
            NewRequestsScreen()
        }

        // Profile screen
        composable(AppScreens.Profile.route) {
            ProfileScreen()
        }

        // Property Detail screen
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
