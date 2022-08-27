package com.woowahan.banchan.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.woowahan.banchan.util.NotificationUtil
import com.woowahan.domain.usecase.order.UpdateOrderUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DeliveryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val updateOrderUseCase: UpdateOrderUseCase
): CoroutineWorker(context, params) {

    companion object {
        private const val EXTRA_ORDER_ID = "order_id"
        private const val EXTRA_ORDER_TITLE = "order_title"

        fun getRequest(
            vararg orderId: Long,
            orderTitle: String?
        ): WorkRequest{
           return OneTimeWorkRequestBuilder<DeliveryWorker>()
                .setInputData(workDataOf(
                    EXTRA_ORDER_ID to orderId,
                    EXTRA_ORDER_TITLE to orderTitle,
                ))
               .build()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    override suspend fun doWork(): Result {
        Timber.d("Delivery background debug => doWork")
        val orderId = inputData.getLongArray(EXTRA_ORDER_ID)
        val orderTitle = inputData.getString(EXTRA_ORDER_TITLE)
        if(orderId == null) return Result.failure()
        updateOrderUseCase(orderId = orderId, false)
            .collect {
                it.onSuccess {
                    Timber.d("Delivery background debug => useCase finish => $it")
                    if(orderId.isNotEmpty()) // 여러개를 동시에 처리중이라면 첫번째 알림만 보낸다
                        showDeliveryNotification(orderId.first(), orderTitle)
                }
            }
        Timber.d("Delivery background debug = > worker finish")
        return Result.success()
    }

    private fun showDeliveryNotification(orderId: Long, orderTitle: String?){
        Timber.d("orderId[$orderId], orderTitle[$orderTitle]")
        NotificationUtil.createNotification(
            context,
            orderTitle,
            orderId
        )
    }
}