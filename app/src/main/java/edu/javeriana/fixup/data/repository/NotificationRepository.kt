package edu.javeriana.fixup.data.repository

import android.util.Log
import edu.javeriana.fixup.data.datasource.interfaces.NotificationDataSource
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.FollowNotificationDto
import edu.javeriana.fixup.data.network.dto.LikeNotificationDto
import edu.javeriana.fixup.data.network.dto.NotificationDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: FixUpApiService,
    private val notificationDataSource: NotificationDataSource
) {
    suspend fun notifyLike(reviewId: String, likerId: String, likerName: String, targetUserId: String) {
        try {
            apiService.notifyLike(LikeNotificationDto(reviewId, likerId, likerName, targetUserId))
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error enviando notificación de like", e)
        }
    }

    suspend fun notifyFollow(targetUserId: String, followerName: String) {
        try {
            apiService.notifyFollow(FollowNotificationDto(targetUserId, followerName))
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error enviando notificación de follow", e)
        }
    }

    fun getNotifications(userId: String): Flow<Result<List<NotificationDto>>> {
        return notificationDataSource.getNotifications(userId)
    }

    suspend fun saveNotification(userId: String, notification: NotificationDto): Result<Unit> {
        return notificationDataSource.saveNotification(userId, notification)
    }

    suspend fun markAsRead(userId: String, notificationId: String): Result<Unit> {
        return notificationDataSource.markAsRead(userId, notificationId)
    }
}
