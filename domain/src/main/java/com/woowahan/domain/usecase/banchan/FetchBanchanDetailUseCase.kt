package com.woowahan.domain.usecase.banchan

import com.woowahan.domain.model.BanchanDetailModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.BanchanDetailRepository
import com.woowahan.domain.usecase.cart.FetchCartItemsKeyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class FetchBanchanDetailUseCase(
    private val detailRepository: BanchanDetailRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(
        hash: String,
        title: String
    ): Flow<DomainEvent<BanchanDetailModel>> = flow<DomainEvent<BanchanDetailModel>> {
        detailRepository.fetchBanchanDetail(hash, title)
            .combine(fetchCartItemsKeyUseCase()) { banchan, key ->
                banchan.copy(isCartItem = key.getOrThrow().contains(banchan.hash))
            }.collect {
                emit(DomainEvent.Success(it))
            }
    }.catch {
        emit(DomainEvent.failure(it))
    }
}