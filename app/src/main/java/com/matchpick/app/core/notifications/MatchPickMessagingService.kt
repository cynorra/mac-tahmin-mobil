package com.matchpick.app.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matchpick.app.R
import com.matchpick.app.ui.MainActivity

class MatchPickMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "MatchPick"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        val clickAction = remoteMessage.data["action"] ?: "OPEN_APP"

        showNotification(title, body, clickAction)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token'ı Firebase'e göndermek için Spark kotalarını aşmamak adına yerel DataStore'da tutulur,
        // sadece kullanıcı giriş yaptığında user belgesine eklenir.
    }

    private fun showNotification(title: String, body: String, action: String) {
        val channelId = "match_updates_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Match Updates & Results",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Periodic prediction reminders and live match result notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("nav_action", action)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Vektör ikon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
