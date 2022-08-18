package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CartListItemModel>>> {
        return cartRepository.fetchCartItems()
            .map {
                kotlin.runCatching {
                    val list = it.getOrThrow()
                    val price = list.filter { it.isSelected }.sumOf { it.price * it.count }

                    listOf(
                        CartListItemModel.Header(
                            isAllSelected = (list.none { !(it.isSelected) } && list.isNotEmpty())
                        )
                    ) + list.map { CartListItemModel.Content(it) } + listOf(
                        CartListItemModel.Footer(
                            price = price
                        )
                    )
                }
            }
    }
}