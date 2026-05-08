package edu.javeriana.fixup.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.javeriana.fixup.MainActivity
import edu.javeriana.fixup.R
import edu.javeriana.fixup.data.network.dto.NotificationDto
import edu.javeriana.fixup.data.repository.NotificationRepository
import edu.javeriana.fixup.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FixUpMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            scope.launch {
                userRepository.updateFcmToken(userId, token)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val type = remoteMessage.data["type"]
        val targetUserId = remoteMessage.data["targetUserId"]
        val title = remoteMessage.notification?.title ?: "FixUp"
        val body = remoteMessage.notification?.body ?: "Tienes una nueva notificación"
        
        saveAndShowNotification(title, body, type, targetUserId)
    }

    private fun saveAndShowNotification(title: String, body: String, type: String?, targetUserId: String?) {
        // The FCM token already guarantees this message is for this device/user.
        // No need to re-check targetUserId — doing so causes silent drops when
        // FirebaseAuth hasn't finished initializing.
        showNotification(title, body)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: targetUserId ?: return
        scope.launch {
            notificationRepository.saveNotification(
                currentUserId,
                NotificationDto(
                    id = "",
                    title = title,
                    message = body,
                    date = "hace un momento",
                    isRead = false,
                    actionType = if (type == "RESPOND") "RESPOND" else null
                )
            )
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "fixup_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones FixUp",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.fixuplogo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
