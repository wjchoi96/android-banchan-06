package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository

class UpdateCartItemSelectUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(hash: String, isSelect: Boolean): Result<Boolean> {
        return kotlin.runCatching {
            cartRepository.updateCartItemSelect(isSelect, hash).getOrThrow()
        }
    }
}