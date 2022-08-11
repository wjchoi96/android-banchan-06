package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.CartRepository

class InsertCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(banchan: BanchanModel, count: Int): Result<Boolean>{
        return kotlin.runCatching {
            cartRepository.insertCartItem(banchan, count).getOrThrow()
        }
    }
}