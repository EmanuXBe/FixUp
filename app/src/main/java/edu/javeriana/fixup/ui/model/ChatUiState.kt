package edu.javeriana.fixup.ui.model

data class MessageModel(
    val text: String,
    val isMe: Boolean,
    val time: String? = null
)

data class ChatUiState(
    val contactName: String = "Andres Contreras",
    val status: String = "Activo hace 11 minutos",
    val messages: List<MessageModel> = emptyList(),
    val currentMessage: String = ""
)
