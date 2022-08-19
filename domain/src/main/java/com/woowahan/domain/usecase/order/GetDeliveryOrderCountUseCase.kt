package com.woowahan.domain.usecase.order

import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetDeliveryOrderCountUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Flow<Result<Int>> = flow {
        orderRepository.getDeliveryOrderCount()
            .collect {
                emit(Result.success(it))
            }
    }.catch {
        emit(Result.failure(it))
    }
}