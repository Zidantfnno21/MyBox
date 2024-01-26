package com.example.mybox.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mybox.utils.NOTIFICATION_ID

class NotificationHelper(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        setUpNotificationChannels()
    }

    private fun setUpNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "upload Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showProgressNotification(progress: Int) {
        val builder = NotificationCompat.Builder(context , CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("Uploading...")
            .setContentText("Upload in progress: $progress%")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, progress, false)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun updateNotification(message: String, pendingIntent: PendingIntent) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("Yay, Completed!")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, false)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun failureNotification(message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("Upload Failed")
            .setContentTitle(message)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun dismissNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val CHANNEL_ID = "upload_channel"
    }
}