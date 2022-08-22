package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class InsertCartItemUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(banchan: BaseBanchan, count: Int): Flow<DomainEvent<Boolean>> = flow<DomainEvent<Boolean>> {
        cartRepository.insertCartItem(banchan, count)
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}