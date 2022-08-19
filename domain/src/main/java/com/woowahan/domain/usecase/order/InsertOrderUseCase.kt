package com.woowahan.domain.usecase.order

import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class InsertOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        time: String,
        hash: String,
        imageUrl: String,
        title: String,
        count: Int,
        price: Long
    ): Flow<Result<Long>> = flow {
        orderRepository.insertOrder(
            time, hash, imageUrl, title, count, price
        ).collect {
            emit(Result.success(it))
        }
    }.catch {
        emit(Result.failure(it))
    }
}