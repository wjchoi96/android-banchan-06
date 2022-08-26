package com.woowahan.domain.usecase.order

import androidx.paging.PagingData
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderModel
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchOrderPagingUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(): Flow<DomainEvent<PagingData<OrderModel>>> = flow<DomainEvent<PagingData<OrderModel>>> {
        orderRepository.fetchOrdersPaging()
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}