package com.woowahan.domain.usecase.cart

import com.woowahan.domain.repository.CartRepository

class FetchCartItemsKeyUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Set<String>>{
        return kotlin.runCatching {
            cartRepository.fetchCartItemsKey().getOrThrow()
        }
    }
}