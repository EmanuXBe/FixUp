package edu.javeriana.fixup.ui.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val requests = listOf(
                NotificationItemModel(
                    id = "1",
                    name = "Andres Contreras",
                    time = "hace 2 min",
                    description = "Aceptó tu propuesta de remodelación.",
                    profileImage = R.drawable.pf1,
                    showButton = true
                ),
                NotificationItemModel(
                    id = "2",
                    name = "Marta Lucía",
                    time = "hace 1 hora",
                    description = "Te envió un nuevo mensaje sobre la cocina.",
                    profileImage = R.drawable.pf2,
                    hasPreview = true,
                    previewImage = R.drawable.cocina
                )
            )
            _uiState.value = NotificationsUiState(requests = requests, isLoading = false)
        }
    }
}
