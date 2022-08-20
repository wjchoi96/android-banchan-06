package com.woowahan.domain.usecase.order

import com.woowahan.domain.model.CartModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.model.OrderItemModel
import com.woowahan.domain.repository.OrderRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        time: Date,
        items: List<CartModel>
    ): Flow<DomainEvent<Long>> = flow<DomainEvent<Long>> {
        orderRepository.insertOrder(
            BanchanDateConvertUtil.convert(time),
            items.map { OrderItemModel(
                it.hash,
                it.imageUrl,
                it.title,
                it.count,
                it.price
            ) }
        ).collect {
            emit(DomainEvent.success(it))
        }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}