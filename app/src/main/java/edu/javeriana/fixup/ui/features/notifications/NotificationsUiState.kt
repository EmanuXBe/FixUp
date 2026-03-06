package edu.javeriana.fixup.ui.features.notifications

data class NotificationsUiState(
    val requests: List<NotificationItemModel> = emptyList(),
    val isLoading: Boolean = false
)

data class NotificationItemModel(
    val id: String,
    val name: String,
    val time: String,
    val description: String,
    val profileImage: Int,
    val showButton: Boolean = false,
    val hasPreview: Boolean = false,
    val previewImage: Int? = null
)
