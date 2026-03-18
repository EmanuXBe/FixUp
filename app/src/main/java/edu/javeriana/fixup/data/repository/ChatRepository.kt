package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.ChatDataSource
import edu.javeriana.fixup.ui.features.chat.MessageModel

class ChatRepository(
    private val dataSource: ChatDataSource = ChatDataSource()
) {
    fun getMessages(): Result<List<MessageModel>> {
        return try {
            val messages = dataSource.getMessages()
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun sendMessage(message: String): Result<MessageModel> {
        return try {
            val newMessage = MessageModel(text = message, isMe = true)
            dataSource.sendMessage(newMessage)
            Result.success(newMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
