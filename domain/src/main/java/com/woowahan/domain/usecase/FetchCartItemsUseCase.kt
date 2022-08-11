package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.CartRepository

class FetchCartItemsUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Map<String, Pair<BanchanModel, Int>>>{
        return kotlin.runCatching {
            cartRepository.fetchCartItems().getOrThrow()
        }
    }
}