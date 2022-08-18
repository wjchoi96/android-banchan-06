package com.woowahan.domain.usecase.cart

import com.woowahan.domain.repository.CartRepository

class UpdateCartItemCountUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hash: String, count: Int): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.updateCartItemCount(hash, count).getOrThrow()
        }
    }
}