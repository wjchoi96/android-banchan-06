package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class RemoveCartItemsUseCase (
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hashes: List<String>): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.removeCartItems(hashes).getOrThrow()
        }
    }
}