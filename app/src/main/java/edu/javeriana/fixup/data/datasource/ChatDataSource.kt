package edu.javeriana.fixup.data.datasource

import edu.javeriana.fixup.ui.features.chat.MessageModel

/**
 * Contrato del Data Source para Chat.
 */
interface ChatDataSource {
    fun getMessages(): List<MessageModel>
    fun sendMessage(message: MessageModel)
}
