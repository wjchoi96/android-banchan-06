package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchSoupDishBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
) {
    suspend operator fun invoke(): Result<List<BanchanModel>>{
        return kotlin.runCatching {
            val cart = fetchCartItemsKeyUseCase().getOrThrow()
            listOf(
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
            ) + banchanRepository.fetchSoupDishBanchan().getOrThrow().map {
                if(cart.contains(it.hash))
                    it.copy(isCartItem = true)
                else
                    it
            }
        }
    }
}