package com.woowahan.domain.usecase

import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class GetCartItemsSizeFlowUseCase(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Int> = cartRepository.getCartSizeFlow()
}