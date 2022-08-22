package com.woowahan.banchan.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.woowahan.banchan.R
import com.woowahan.banchan.ui.order.OrderItemActivity

object NotificationUtil {
    private val channelId = "orderChannel"

    fun createNotification(
        context: Context,
        itemName: String?,
        orderId: Long,
    ) {
        val navigateIntent = OrderItemActivity.get(context, orderId = orderId)
        val resultPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(navigateIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_MUTABLE)
            } else {
                getPendingIntent(
                    System.currentTimeMillis().toInt(),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.order_complete))
            .setContentText("${itemName ?: "상품"}의 ${context.getString(R.string.order_complete)}")
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_VIBRATE)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelId, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}