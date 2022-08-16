package com.woowahan.domain.usecase

import com.woowahan.domain.model.CartListModel
import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CartListModel>>> {
        return cartRepository.fetchCartItems()
            .map {
                kotlin.runCatching {
                    val list = it.getOrThrow()
                    val price = list.sumOf { it.price * it.count }
                    val deliveryFee = 2500L

                    listOf(CartListModel.Header) + list.map { CartListModel.Content(it) } + listOf(
                        CartListModel.Footer(
                            price = price,
                            deliveryFee = deliveryFee,
                            totalPrice = price + deliveryFee
                        )
                    )
                }
            }
    }
}