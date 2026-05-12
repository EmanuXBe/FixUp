package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.javeriana.fixup.data.datasource.interfaces.ChatDataSource
import edu.javeriana.fixup.ui.features.chat.MessageModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementación de ChatDataSource usando Firebase Firestore para tiempo real.
 */
class ChatDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatDataSource {

    override fun getMessages(): Flow<List<MessageModel>> = callbackFlow {
        val subscription = firestore.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    val text = doc.getString("text") ?: ""
                    val isMe = doc.getBoolean("isMe") ?: false
                    MessageModel(text = text, isMe = isMe)
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(message: MessageModel) {
        val data = mapOf(
            "text" to message.text,
            "isMe" to message.isMe,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("messages").add(data).await()
    }
}
