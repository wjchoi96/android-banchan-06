package com.woowahan.domain.usecase.banchan

import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.usecase.cart.FetchCartItemsKeyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class FetchBestBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
){
    suspend operator fun invoke(): Flow<DomainEvent<List<BestBanchanModel>>> = flow<DomainEvent<List<BestBanchanModel>>> {
        banchanRepository.fetchBestBanchan().combine(fetchCartItemsKeyUseCase()) { banchan, key ->
            val cart = key.getOrThrow()
            listOf(
                BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)
            ) + banchan.map {
                it.copy(banchans = it.banchans.map { banchan ->
                    if (cart.contains(banchan.hash)) {
                        banchan.copy(isCartItem = true)
                    } else
                        banchan
                })
            }
        }.collect{
            emit(DomainEvent.success(it))
        }
    }.catch {
        emit(DomainEvent.failure(it, listOf(
            BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)))
        )
    }
}