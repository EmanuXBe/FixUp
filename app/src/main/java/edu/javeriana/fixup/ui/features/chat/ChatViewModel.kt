package edu.javeriana.fixup.ui.features.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        val messages = listOf(
            MessageModel(text = "Hola! quisiera saber mas sobre tu solicitud", isMe = true),
            MessageModel(text = "Hola", isMe = false),
            MessageModel(text = "Quisiera remodelar el piso de mi apartamento", isMe = false),
            MessageModel(text = "Cuando podrias pasar?", isMe = false),
            MessageModel(text = "Claro! podria pasar el lunes", isMe = true),
            MessageModel(text = "Y asi hacer una cotizacion", isMe = true),
            MessageModel(text = "Ok", isMe = false),
            MessageModel(text = "perfecto!", isMe = false),
            MessageModel(text = "el lunes estaria perfecto, estare atento", isMe = false)
        )
        _uiState.update { it.copy(messages = messages) }
    }

    fun onMessageChanged(newMessage: String) {
        _uiState.update { it.copy(currentMessage = newMessage) }
    }

    fun sendMessage() {
        if (_uiState.value.currentMessage.isNotBlank()) {
            val newMessage = MessageModel(text = _uiState.value.currentMessage, isMe = true)
            _uiState.update { 
                it.copy(
                    messages = it.messages + newMessage,
                    currentMessage = ""
                )
            }
        }
    }
}
