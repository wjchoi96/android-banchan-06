package com.woowahan.domain.usecase.order

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetDeliveryOrderCountUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Flow<DomainEvent<Int>> = flow<DomainEvent<Int>> {
        orderRepository.getDeliveryOrderCount()
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it, 0))
    }
}