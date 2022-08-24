package com.woowahan.banchan.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.woowahan.banchan.R
import java.util.*

object DeliveryRequester {
    private const val testDeliveryMill: Long = 1000*10

    fun setDeliveryAlarm(
        context: Context,
        orderId: Long,
        orderTitle: String?,
        orderItemCount: Int,
        minute: Int
    ) {
        val title = context.getString(R.string.order_items_title, orderTitle ?: "상품", orderItemCount)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().time.time + (minute * 60) * 1000,
            PendingIntent.getBroadcast(
                context,
                orderId.toInt(),
                DeliveryAlarmReceiver.get(context, orderId, title),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}