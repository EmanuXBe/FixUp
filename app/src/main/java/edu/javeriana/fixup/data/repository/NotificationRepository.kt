package edu.javeriana.fixup.data.repository

import android.util.Log
import edu.javeriana.fixup.data.network.api.FixUpApiService
import edu.javeriana.fixup.data.network.dto.FollowNotificationDto
import edu.javeriana.fixup.data.network.dto.LikeNotificationDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: FixUpApiService
) {
    suspend fun notifyLike(reviewId: String, likerId: String, likerName: String) {
        try {
            apiService.notifyLike(LikeNotificationDto(reviewId, likerId, likerName))
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
}
