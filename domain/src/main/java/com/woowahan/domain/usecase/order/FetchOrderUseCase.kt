package com.woowahan.domain.usecase.order

import com.woowahan.domain.model.OrderItemTypeModel
import com.woowahan.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: Long): Flow<Result<List<OrderItemTypeModel>>> = flow {
        orderRepository.fetchOrder(orderId)
            .collect {
                val list = listOf(
                    OrderItemTypeModel.Header(
                        "20분", // test
                        it.items.size
                    )
                ) + it.items.map { item ->
                    OrderItemTypeModel.Order(item)
                } + listOf(
                    OrderItemTypeModel.Footer(
                        it.items.sumOf { item -> item.price }
                    )
                )
                emit(Result.success(list))
            }
    }.catch {
        emit(Result.failure(it))
    }
}