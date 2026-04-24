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
import edu.javeriana.fixup.ui.features.following_feed.FollowingFeedScreen
import edu.javeriana.fixup.ui.features.rent.CreatePropertyScreen
import edu.javeriana.fixup.ui.features.rent.RentScreen
import edu.javeriana.fixup.ui.features.splash.SplashScreen
import edu.javeriana.fixup.ui.features.user_profile.UserProfileScreen
import edu.javeriana.fixup.ui.features.followlist.FollowListScreen

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
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onNavigateToFeed = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AppScreens.LogIn.route) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreens.LogIn.route) {
            LogInScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(AppScreens.Register.route) }
            )
        }

        composable(AppScreens.Register.route) {
            RegisterScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppScreens.Feed.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreens.Feed.route) {
            FeedScreen(
                viewModel = hiltViewModel(),
                onPublicationClick = { id -> navController.navigate(AppScreens.Publication.route + "/$id") },
                onAllPublicationsClick = { navController.navigate(AppScreens.AllPublications.route) },
                onFollowingClick = { navController.navigate(AppScreens.FollowingFeed.route) }
            )
        }

        composable(AppScreens.Profile.route) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onSettingsClick = { navController.navigate(AppScreens.Settings.route) },
                onNavigateToFollowList = { uid, type ->
                    navController.navigate("followList/$uid/$type")
                }
            )
        }

        composable(
            route = "followList/{userId}/{listType}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("listType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FollowListScreen(
                onNavigateBack = { navController.popBackStack() },
                onUserClick = { uid ->
                    navController.navigate("${AppScreens.UserProfile.route}/$uid")
                }
            )
        }

        composable(
            route = "${AppScreens.UserProfile.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserProfileScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() },
                onServiceClick = { serviceId ->
                    navController.navigate(AppScreens.Publication.route + "/$serviceId")
                },
                viewModel = hiltViewModel()
            )
        }

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

        // --- Rutas restantes (Rent, Chat, Checkout, etc.) ---
        composable(AppScreens.Rent.route) {
            RentScreen(
                viewModel = hiltViewModel(),
                onSelectClick = { id -> navController.navigate(AppScreens.PropertyDetail.route + "/$id") },
                onCreateClick = { navController.navigate(AppScreens.CreatePublication.route) }
            )
        }

        composable(AppScreens.CreatePublication.route) {
            CreatePropertyScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }

        composable(AppScreens.Chat.route) {
            ChatScreen(viewModel = hiltViewModel(), onBackClick = { navController.popBackStack() })
        }

        composable(AppScreens.Checkout.route) {
            CheckoutScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.navigate(AppScreens.Chat.route) }
            )
        }

        composable(AppScreens.Notifications.route) {
            NotificationsScreen(viewModel = hiltViewModel())
        }

        composable(AppScreens.AllPublications.route) {
            AllPublicationsScreen(onPublicationClick = { id -> navController.navigate(AppScreens.Publication.route + "/$id") })
        }

        composable(AppScreens.FollowingFeed.route) {
            FollowingFeedScreen(
                viewModel = hiltViewModel(),
                onBackClick = { navController.popBackStack() },
                onPublicationClick = { id -> navController.navigate(AppScreens.Publication.route + "/$id") }
            )
        }
    }
}