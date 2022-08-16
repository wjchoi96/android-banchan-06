package com.woowahan.domain.usecase

import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CartModel>>> {
        return cartRepository.fetchCartItems()
            .map {
                kotlin.runCatching {
                    listOf(CartModel.header()) + it.getOrThrow() + listOf(CartModel.footer())
                }
            }
    }
}