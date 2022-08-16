package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class UpdateCartItemsSelectUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hashes: List<String>, isSelect: Boolean): Result<Boolean> {
        return kotlin.runCatching {
            cartRepository.updateCartItemsSelect(hashes, isSelect).getOrThrow()
        }
    }
}