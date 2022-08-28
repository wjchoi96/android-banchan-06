package com.woowahan.banchan.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import timber.log.Timber

class DeliveryAlarmReceiver: BroadcastReceiver() {
    companion object {
        private const val EXTRA_ORDER_ID = "order_id"
        private const val EXTRA_ORDER_TITLE = "order_title"

        fun get(context: Context, orderId: Long, orderTitle: String): Intent = Intent(
            context,
            DeliveryAlarmReceiver::class.java
        ).apply {
            putExtra(EXTRA_ORDER_ID, orderId)
            putExtra(EXTRA_ORDER_TITLE, orderTitle)
        }
    }
    override fun onReceive(p0: Context?, p1: Intent?) {
        Timber.d("Delivery background debug => DeliveryAlarmReceiver onReceive => ###############")
        if(p0 == null) return
        p1?.let {
            val orderId = it.getLongExtra(EXTRA_ORDER_ID, 0)
            val orderTitle = it.getStringExtra(EXTRA_ORDER_TITLE)
            setDeliveryWorker(p0, orderId, orderTitle)
        }
    }

    private fun setDeliveryWorker(context: Context, orderId: Long, orderTitle: String?){
        val deliveryWorkRequest = DeliveryWorker.getRequest(orderId = longArrayOf(orderId), orderTitle)
        WorkManager.getInstance(context.applicationContext)
            .enqueue(deliveryWorkRequest)
    }
}