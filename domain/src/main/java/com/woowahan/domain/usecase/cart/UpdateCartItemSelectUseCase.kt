package com.woowahan.domain.usecase.cart

import com.woowahan.domain.repository.CartRepository

class UpdateCartItemSelectUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(isSelect: Boolean, vararg hashes: String): Result<Boolean> {
        return kotlin.runCatching {
            cartRepository.updateCartItemSelect(isSelect, *hashes).getOrThrow()
        }
    }
}