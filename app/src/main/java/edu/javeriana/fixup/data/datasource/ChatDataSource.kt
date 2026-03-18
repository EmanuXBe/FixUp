package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.features.chat.MessageModel

class ChatDataSource {
    fun getMessages(): List<MessageModel> {
        return listOf(
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
    }

    fun sendMessage(message: MessageModel) {
        // En una app real, aquí se enviaría a Firestore
    }
}
