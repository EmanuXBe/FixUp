package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.ChatDataSource
import edu.javeriana.fixup.ui.features.chat.MessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val dataSource: ChatDataSource
) {
    fun getMessages(): Flow<Result<List<MessageModel>>> {
        return dataSource.getMessages()
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    suspend fun sendMessage(message: String): Result<Unit> {
        return try {
            val newMessage = MessageModel(text = message, isMe = true)
            dataSource.sendMessage(newMessage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
