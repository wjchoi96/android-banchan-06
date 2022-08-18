package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class RemoveCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(vararg hashes: String): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.removeCartItem(*hashes).getOrThrow()
        }
    }
}