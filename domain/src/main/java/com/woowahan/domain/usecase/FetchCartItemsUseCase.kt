package com.woowahan.domain.usecase

import com.woowahan.domain.model.CartModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CartModel>>> {
        return cartRepository.fetchCartItems()
    }
}