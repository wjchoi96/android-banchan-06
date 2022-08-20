package com.woowahan.domain.usecase.order

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderModel
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Flow<DomainEvent<List<OrderModel>>> = flow<DomainEvent<List<OrderModel>>> {
        orderRepository.fetchOrders()
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}