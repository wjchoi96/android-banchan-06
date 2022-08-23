package com.woowahan.domain.usecase.order

import com.woowahan.domain.constant.DeliveryConstant
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderItemTypeModel
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class FetchOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: Long): Flow<DomainEvent<List<OrderItemTypeModel>>> = flow<DomainEvent<List<OrderItemTypeModel>>> {
        orderRepository.fetchOrder(orderId)
            .collect {
                val list = listOf(
                    OrderItemTypeModel.Header(
                        it.deliveryState,
                        it.time,
                        Calendar.getInstance().time, // flow 는 주소값이 달라도 내부값이 동일하면 emit 되지않는데, 그걸 방지하기 위함
                        DeliveryConstant.DeliveryMinute,
                        it.items.size
                    )
                ) + it.items.map { item ->
                    OrderItemTypeModel.Order(item)
                } + listOf(
                    it.items.sumOf { item -> item.price * item.count }.run {
                        OrderItemTypeModel.Footer(
                            this,
                            if(this >= DeliveryConstant.FreeDeliveryFeePrice) DeliveryConstant.FreeDeliveryFeePrice else DeliveryConstant.DeliveryFee
                        )
                    }
                )
                emit(DomainEvent.success(list))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}