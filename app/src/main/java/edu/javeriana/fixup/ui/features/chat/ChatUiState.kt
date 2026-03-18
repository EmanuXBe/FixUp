package edu.javeriana.fixup.ui.features.chat

data class ChatUiState(
    val contactName: String = "Juan Sebastian",
    val status: String = "En línea",
    val messages: List<MessageModel> = emptyList(),
    val currentMessage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MessageModel(
    val text: String,
    val isMe: Boolean
)
