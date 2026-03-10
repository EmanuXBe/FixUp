package edu.javeriana.fixup.ui.features.main

import androidx.lifecycle.ViewModel
import edu.javeriana.fixup.navigation.AppScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateCurrentRoute(route: String?) {
        val showBottomNav = route in listOf(
            AppScreens.Feed.route,
            AppScreens.Rent.route,
            AppScreens.Notifications.route,
            AppScreens.Profile.route
        )

        val topBarTitle = when {
            route?.startsWith(AppScreens.Publication.route) == true -> "Detalle de Publicación"
            route == AppScreens.AllPublications.route -> "Publicaciones"
            route == AppScreens.Checkout.route -> "Pantalla de pago"
            route == AppScreens.Chat.route -> "Chat"
            else -> null
        }

        val showBackButton = route !in listOf(
            AppScreens.LogIn.route,
            AppScreens.Register.route,
            AppScreens.Feed.route,
            AppScreens.Rent.route,
            AppScreens.Notifications.route,
            AppScreens.Profile.route
        )

        _uiState.value = MainUiState(
            currentRoute = route,
            showBottomNav = showBottomNav,
            topBarTitle = topBarTitle,
            showBackButton = showBackButton
        )
    }
}

data class MainUiState(
    val currentRoute: String? = null,
    val showBottomNav: Boolean = false,
    val topBarTitle: String? = null,
    val showBackButton: Boolean = false
)
