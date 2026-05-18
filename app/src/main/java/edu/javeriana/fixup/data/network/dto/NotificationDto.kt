package edu.javeriana.fixup.data.network.dto

data class LikeNotificationDto(
    val reviewId: String,
    val likerId: String,
    val likerName: String,
    val targetUserId: String, // User who owns the review and should receive the notification
    /** Foto de perfil del liker. El backend la propaga en el payload FCM data para que el receptor
     * la guarde sin necesidad de un lookup posterior a Firestore. */
    val likerProfileImageUrl: String? = null
)

data class FollowNotificationDto(
    val targetUserId: String,
    val followerName: String,
    val followerId: String? = null,
    val followerProfileImageUrl: String? = null
)

data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean,
    val profileImageUrl: String? = null,
    val previewImageUrl: String? = null,
    val actionType: String? = null, // e.g., "RESPOND", "VIEW", "LIKE"
    /** UID del usuario que originó la notificación (ej. quien dio like / follow). */
    val actorId: String? = null
)
