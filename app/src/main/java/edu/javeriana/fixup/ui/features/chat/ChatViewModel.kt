package edu.javeriana.fixup.ui.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.javeriana.fixup.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        _uiState.update { it.copy(isLoading = true) }
        val result = repository.getMessages()
        
        result.onSuccess { messages ->
            _uiState.update { it.copy(messages = messages, isLoading = false) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message, isLoading = false) }
        }
    }

    fun onMessageChanged(newMessage: String) {
        _uiState.update { it.copy(currentMessage = newMessage) }
    }

    fun sendMessage() {
        val currentText = _uiState.value.currentMessage
        if (currentText.isNotBlank()) {
            val result = repository.sendMessage(currentText)
            
            result.onSuccess { newMessage ->
                _uiState.update { 
                    it.copy(
                        messages = it.messages + newMessage,
                        currentMessage = ""
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }
}
