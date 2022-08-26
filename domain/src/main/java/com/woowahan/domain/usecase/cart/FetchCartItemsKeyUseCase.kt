package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchCartItemsKeyUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<DomainEvent<Set<String>>> = flow<DomainEvent<Set<String>>>{
        cartRepository.fetchCartItemsKey()
            .collect { emit(DomainEvent.success(it)) }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}