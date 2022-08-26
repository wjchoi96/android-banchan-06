package com.woowahan.domain.usecase.cart

import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetCartItemsSizeFlowUseCase(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<DomainEvent<Int>> = flow<DomainEvent<Int>> { // 해당 block 내부에서 에러가 발생한다면 아래 catch 로 잡힌다
        cartRepository.getCartSizeFlow()
            .collect {
                emit(DomainEvent.success(it))
            }
    }.catch {
        emit(DomainEvent.failure(Throwable(it)))
    }
}
