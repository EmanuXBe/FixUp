package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.ui.features.chat.MessageModel

import kotlinx.coroutines.flow.Flow

/**
 * Contrato del Data Source para Chat.
 */
interface ChatDataSource {
    fun getMessages(): Flow<List<MessageModel>>
    suspend fun sendMessage(message: MessageModel)
}
