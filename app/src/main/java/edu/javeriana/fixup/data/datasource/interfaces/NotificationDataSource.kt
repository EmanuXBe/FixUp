package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.data.network.dto.NotificationDto
import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    fun getNotifications(userId: String): Flow<Result<List<NotificationDto>>>
    suspend fun saveNotification(userId: String, notification: NotificationDto): Result<Unit>
    suspend fun markAsRead(userId: String, notificationId: String): Result<Unit>
}
