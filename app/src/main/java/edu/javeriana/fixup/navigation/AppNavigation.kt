package edu.javeriana.fixup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.javeriana.fixup.ui.features.auth.login.LogInScreen
import edu.javeriana.fixup.ui.features.auth.register.RegisterScreen
import edu.javeriana.fixup.ui.features.chat.ChatScreen
import edu.javeriana.fixup.ui.features.checkout.CheckoutScreen
import edu.javeriana.fixup.ui.features.feed.AllPublicationsScreen
import edu.javeriana.fixup.ui.features.feed.FeedScreen
import edu.javeriana.fixup.ui.features.notifications.NotificationsScreen
import edu.javeriana.fixup.ui.features.profile.ProfileScreen
import edu.javeriana.fixup.ui.features.profile.SettingsScreen
import edu.javeriana.fixup.ui.features.property_detail.PropertyDetailScreen
import edu.javeriana.fixup.ui.features.publication_detail.PublicationDetailScreen
import edu.javeriana.fixup.ui.features.rent.CreatePropertyScreen
import edu.javeriana.fixup.ui.features.rent.RentScreen
import edu.javeriana.fixup.ui.features.splash.SplashScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route,
        modifier = modifier
    ) {
        // Splash screen
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onNavigateToFeed = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true } }
                },
                onNavigateToLogin = {
                    navController.navigate(AppScreens.LogIn.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true } }
                }
            )
        }

        // Login screen
        composable(AppScreens.LogIn.route) {
            LogInScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true } }
                },
                onRegisterClick = { navController.navigate(AppScreens.Register.route) }
            )
        }

        // Register screen
        composable(AppScreens.Register.route) {
            RegisterScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true } }
                }
            )
        }

        // Feed screen
        composable(AppScreens.Feed.route) {
            FeedScreen(
                viewModel = hiltViewModel(),
                onPublicationClick = { id ->
                    navController.navigate(AppScreens.Publication.route + "/$id")
                },
                onAllPublicationsClick = {
                    navController.navigate(AppScreens.AllPublications.route)
                }
            )
        }

        // Rent screen
        composable(AppScreens.Rent.route) {
            RentScreen(
                viewModel = hiltViewModel(),
                onSelectClick = { id ->
                    navController.navigate(AppScreens.PropertyDetail.route + "/$id")
                },
                onCreateClick = {
                    navController.navigate(AppScreens.CreatePublication.route)
                }
            )
        }

        // Create Publication screen
        composable(AppScreens.CreatePublication.route) {
            CreatePropertyScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }

        // Notifications screen
        composable(AppScreens.Notifications.route) {
            NotificationsScreen(viewModel = hiltViewModel())
        }

        // Profile screen
        composable(AppScreens.Profile.route) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onSettingsClick = {
                    navController.navigate(AppScreens.Settings.route)
                }
            )
        }

        // Settings screen
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onLogout = {
                    navController.navigate(AppScreens.LogIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Property Detail screen
        composable(
            route = AppScreens.PropertyDetail.route + "/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            PropertyDetailScreen(
                propertyId = propertyId,
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onReserveClick = { navController.navigate(AppScreens.Checkout.route) }
            )
        }

        // Publication screen
        composable(
            route = AppScreens.Publication.route + "/{publicationId}",
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getString("publicationId")
            PublicationDetailScreen(
                publicationId = publicationId,
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onContactClick = { navController.navigate(AppScreens.Checkout.route) }
            )
        }

        // All Publications screen
        composable(AppScreens.AllPublications.route) {
            AllPublicationsScreen(
                onPublicationClick = { id ->
                    navController.navigate(AppScreens.Publication.route + "/$id")
                }
            )
        }

        // Checkout screen
        composable(AppScreens.Checkout.route) {
            CheckoutScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.navigate(AppScreens.Chat.route) }
            )
        }

        // Chat screen
        composable(AppScreens.Chat.route) {
            ChatScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
