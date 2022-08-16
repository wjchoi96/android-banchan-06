package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.BanchanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchMainDishBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(): Flow<Result<List<BanchanModel>>> {
        return banchanRepository.fetchMainDishBanchan()
            .map {
                kotlin.runCatching {
                    val cart = fetchCartItemsKeyUseCase().getOrThrow()
                    listOf(
                        BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                        BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
                    ) + it.getOrThrow().map {
                        if(cart.contains(it.hash))
                            it.copy(isCartItem = true)
                        else
                            it
                    }
                }
            }
    }
}