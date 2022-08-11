package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class RemoveCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hash: String): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.removeCartItem(hash).getOrThrow()
        }
    }
}