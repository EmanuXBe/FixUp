package edu.javeriana.fixup.ui.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.AuthRepository
import edu.javeriana.fixup.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun markAsRead(notificationId: String) {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            notificationRepository.markAsRead(userId, notificationId)
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: ""
            if (userId.isEmpty()) {
                _uiState.value = NotificationsUiState(isLoading = false)
                return@launch
            }

            notificationRepository.getNotifications(userId).collectLatest { result ->
                result.onSuccess { notifications ->
                    val mappedRequests = notifications.map { dto ->
                        NotificationItemModel(
                            id = dto.id,
                            title = dto.title,
                            message = dto.message,
                            date = dto.date,
                            isRead = dto.isRead,
                            profileImageUrl = dto.profileImageUrl,
                            showButton = dto.actionType == "RESPOND",
                            hasPreview = dto.previewImageUrl != null,
                            previewImageUrl = dto.previewImageUrl
                        )
                    }
                    _uiState.value = NotificationsUiState(requests = mappedRequests, isLoading = false)
                }.onFailure {
                    _uiState.value = NotificationsUiState(isLoading = false)
                }
            }
        }
    }
}
