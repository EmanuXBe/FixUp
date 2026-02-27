package edu.javeriana.fixup.navigation

/**
 * Enum class representing all screens in the FixUp app.
 * Each entry has a [route] string used by the navigation graph.
 */
enum class AppScreens(val route: String) {
    LogIn("login"),
    Register("register"),
    Feed("feed"),
    Profile("profile"),
    Rent("rent"),
    PropertyDetail("property_detail"),
    Publication("publication")
}
