package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateCartItemSelectUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(isSelect: Boolean, vararg hashes: String): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        cartRepository.updateCartItemSelect(isSelect, *hashes)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}