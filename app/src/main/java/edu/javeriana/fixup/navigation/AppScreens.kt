package edu.javeriana.fixup.navigation

/**
 * Sealed class representing all screens in the FixUp app.
 * Each entry has a [route] string used by the navigation graph.
 */
sealed class AppScreens(val route: String) {
    data object LogIn : AppScreens("login")
    data object Register : AppScreens("register")
    data object Feed : AppScreens("feed")
    data object Profile : AppScreens("profile")
    data object Rent : AppScreens("rent")
    data object PropertyDetail : AppScreens("property_detail")
    data object Publication : AppScreens("publication")
    data object AllPublications : AppScreens("all_publications")
    data object Checkout : AppScreens("checkout")
    data object Notifications : AppScreens("notifications")
    data object Chat : AppScreens("chat")
}
