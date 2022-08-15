package com.woowahan.domain.usecase

import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CartModel>>> {
        return cartRepository.fetchCartItems()
    }
}