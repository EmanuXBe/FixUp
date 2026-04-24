package edu.javeriana.fixup.data.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.javeriana.fixup.MainActivity
import edu.javeriana.fixup.R
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            scope.launch {
                userRepository.updateFcmToken(userId, token)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        when (remoteMessage.data["type"]) {
            "LIKE_EVENT" -> {
                val title = remoteMessage.notification?.title ?: "¡Nueva interacción!"
                val body = remoteMessage.notification?.body ?: "A alguien le gustó tu reseña."
                showNotification(title, body)
            }
            "FOLLOW_EVENT" -> {
                val title = remoteMessage.notification?.title ?: "¡Nuevo seguidor!"
                val body = remoteMessage.notification?.body ?: "Alguien comenzó a seguirte."
                showNotification(title, body)
            }
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
