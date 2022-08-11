package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class UpdateCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hash: String, count: Int): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.updateCartItem(hash, count).getOrThrow()
        }
    }
}