package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedItemUseCase
import kotlinx.coroutines.flow.*

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository,
    private val fetchRecentViewedItemUseCase: FetchRecentViewedItemUseCase
) {
    suspend operator fun invoke(): Flow<DomainEvent<List<CartListItemModel>>> =
        flow<DomainEvent<List<CartListItemModel>>> {
            cartRepository.fetchCartItems()
                .combine(fetchRecentViewedItemUseCase(7)) { cartList, recentViewedList ->
                    if (cartList.isEmpty()) {
                        DomainEvent.success(
                            listOf(
                                CartListItemModel.Content(CartModel.empty()),
                                CartListItemModel.Footer(
                                    price = 0L,
                                    recentViewedItems = recentViewedList.getOrThrow(),
                                    showPriceInfo = cartList.isEmpty()
                                )
                            )
                        )
                    } else {
                        val price = cartList.filter { it.isSelected }.sumOf { it.price * it.count }
                        DomainEvent.success(listOf(
                            CartListItemModel.Header(
                                isAllSelected = (cartList.none { !(it.isSelected) } && cartList.isNotEmpty())
                            )
                        ) + cartList.map { CartListItemModel.Content(it) } + listOf(
                            CartListItemModel.Footer(
                                price = price,
                                recentViewedItems = recentViewedList.getOrThrow(),
                                showPriceInfo = cartList.isEmpty()
                            )
                        ))
                    }
                }.collect {
                    emit(it)
                }
        }.catch {
            emit(
                DomainEvent.failure(
                    it, listOf(
                        CartListItemModel.Content(CartModel.empty()),
                        CartListItemModel.Footer(
                            price = 0L,
                            recentViewedItems = emptyList(),
                            showPriceInfo = false
                        )
                    )
                )
            )
        }
}