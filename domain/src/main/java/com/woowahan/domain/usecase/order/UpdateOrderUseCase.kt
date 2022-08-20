package com.woowahan.domain.usecase.order

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        orderId: Long, deliveryState: Boolean
    ): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        orderRepository.updateOrder(
            orderId, deliveryState
        ).collect {
            emit(DomainEvent.success(it))
        }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}