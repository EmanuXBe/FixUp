package edu.javeriana.fixup.data.network.dto

data class LikeNotificationDto(
    val reviewId: String,
    val likerId: String,
    val likerName: String
)

data class FollowNotificationDto(
    val targetUserId: String,
    val followerName: String
)
