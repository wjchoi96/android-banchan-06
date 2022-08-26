package com.woowahan.domain.usecase.cart

import com.woowahan.domain.constant.DeliveryConstant
import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import com.woowahan.domain.usecase.recentviewed.FetchRecentViewedItemUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

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
                                    showPriceInfo = cartList.isNotEmpty(),
                                    deliveryFee = DeliveryConstant.DeliveryFee,
                                    minimumOrderPrice = DeliveryConstant.MinimumOrderPrice,
                                    freeDeliveryFeePrice = DeliveryConstant.FreeDeliveryFeePrice,
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
                                showPriceInfo = cartList.isNotEmpty(),
                                deliveryFee = if (price < DeliveryConstant.FreeDeliveryFeePrice) DeliveryConstant.DeliveryFee else DeliveryConstant.FreeDeliveryFee,
                                minimumOrderPrice = DeliveryConstant.MinimumOrderPrice,
                                freeDeliveryFeePrice = DeliveryConstant.FreeDeliveryFeePrice,
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
                            showPriceInfo = false,
                            deliveryFee = DeliveryConstant.DeliveryFee,
                            minimumOrderPrice = DeliveryConstant.MinimumOrderPrice,
                            freeDeliveryFeePrice = DeliveryConstant.FreeDeliveryFeePrice
                        )
                    )
                )
            )
        }
}