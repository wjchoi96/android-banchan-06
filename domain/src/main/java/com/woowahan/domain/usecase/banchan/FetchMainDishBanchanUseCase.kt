package com.woowahan.domain.usecase.banchan

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.DomainEvent
import com.woowahan.domain.repository.BanchanRepository
import com.woowahan.domain.usecase.cart.FetchCartItemsKeyUseCase
import kotlinx.coroutines.flow.*

class FetchMainDishBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(): Flow<DomainEvent<List<BanchanModel>>> = flow<DomainEvent<List<BanchanModel>>> {
        val cart = fetchCartItemsKeyUseCase().getOrThrow()
        banchanRepository.fetchMainDishBanchan()
            .collect {
                val list = listOf(
                    BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                    BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
                ) + it.map { item ->
                    if (cart.contains(item.hash))
                        item.copy(isCartItem = true)
                    else
                        item
                }
                emit(DomainEvent.success(list))
            }
    }.catch {
        print("catch at useCase => ${it.message}")
        emit(DomainEvent.failure(it, listOf(
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
        )))
    }
}