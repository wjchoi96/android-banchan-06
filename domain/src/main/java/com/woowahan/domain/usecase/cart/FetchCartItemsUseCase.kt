package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<DomainEvent<List<CartListItemModel>>> = flow<DomainEvent<List<CartListItemModel>>> {
        cartRepository.fetchCartItems()
            .collect { list ->
                val price = list.filter { it.isSelected }.sumOf { it.price * it.count }
                emit(DomainEvent.success(listOf(
                    CartListItemModel.Header(
                        isAllSelected = (list.none { !(it.isSelected) } && list.isNotEmpty())
                    )
                ) + list.map { CartListItemModel.Content(it) } + listOf(
                    CartListItemModel.Footer(
                        price = price
                    )
                )))
            }
    }.catch {
        emit(DomainEvent.failure(
            it, listOf(
                CartListItemModel.Header(false),
                CartListItemModel.Footer(
                    price = 0L
                )
            )
        ))
    }
}