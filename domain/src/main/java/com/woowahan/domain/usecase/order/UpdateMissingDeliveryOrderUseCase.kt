package com.woowahan.domain.usecase.order

import com.woowahan.domain.constant.DeliveryConstant
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class UpdateMissingDeliveryOrderUseCase(
    private val orderRepository: OrderRepository,
    private val updateOrderUseCase: UpdateOrderUseCase
) {
    suspend operator fun invoke(): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        orderRepository.fetchDeliveryOrder()
            .collect {
                println("missing debug => delivery count => ${it.size}")
                val missList = mutableListOf<Long>()
                it.forEach { item ->
                    if(checkDeliveryRemainingTime(item.time)){
                        println("missing debug => missing order => $item")
                        missList.add(item.orderId)
                    }
                }
                println("missing debug => missing count => ${missList.size}")
                if(missList.isNotEmpty()){
                    updateOrderUseCase(orderId = missList.toLongArray(), false)
                        .collect { event ->
                            event.onSuccess {
                                println("missing debug => update $it")
                                emit(DomainEvent.success(it))
                            }.onFailure {
                                it.printStackTrace()
                                println("missing debug => update fail => $it")
                                emit(DomainEvent.failure(it))
                            }
                        }
                }else{
                    emit(DomainEvent.success(false))
                }
            }
    }.catch {
        println("missing debug => catch => $it")
        it.printStackTrace()
        emit(DomainEvent.failure(it))
    }

    private fun checkDeliveryRemainingTime(deliveryStartTime: Date?): Boolean{
        if(deliveryStartTime == null) return false
        val current = Calendar.getInstance().time.time
        val start = deliveryStartTime.time
        val lastTimeSinceStartMinute = (((current-start)/1000)/60).toInt() // 배송 시작하고 지난 분
        return lastTimeSinceStartMinute > DeliveryConstant.DeliveryMinute
    }
}