package com.woowahan.domain.usecase.banchan

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.usecase.cart.FetchCartItemsKeyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class FetchSoupDishBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(): Flow<DomainEvent<List<BanchanModel>>> = flow<DomainEvent<List<BanchanModel>>> {
        banchanRepository.fetchSoupDishBanchan().combine(fetchCartItemsKeyUseCase()){ banchan, key ->
            val cart = key.getOrThrow()
            listOf(
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
            ) + banchan.map {
                if(cart.contains(it.hash))
                    it.copy(isCartItem = true)
                else
                    it
            }
        }.collect {
            emit(DomainEvent.success(it))
        }
    }.catch {
        emit(DomainEvent.failure(it, listOf(
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
        )))
    }
}