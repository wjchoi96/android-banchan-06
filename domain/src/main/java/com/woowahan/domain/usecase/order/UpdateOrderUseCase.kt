package com.woowahan.domain.usecase.order

import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        orderId: Long, deliveryState: Boolean
    ): Flow<Result<Boolean>> = flow {
        orderRepository.updateOrder(
            orderId, deliveryState
        ).collect {
            emit(Result.success(it))
        }
    }.catch {
        emit(Result.failure(it))
    }
}