package edu.javeriana.fixup.ui.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.javeriana.fixup.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getMessages().collectLatest { result ->
                result.onSuccess { messages ->
                    _uiState.update { it.copy(messages = messages, isLoading = false) }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            }
        }
    }

    fun onMessageChanged(newMessage: String) {
        _uiState.update { it.copy(currentMessage = newMessage) }
    }

    fun sendMessage() {
        val currentText = _uiState.value.currentMessage
        if (currentText.isNotBlank()) {
            viewModelScope.launch {
                val result = repository.sendMessage(currentText)
                
                result.onSuccess {
                    _uiState.update { it.copy(currentMessage = "") }
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            }
        }
    }
}
