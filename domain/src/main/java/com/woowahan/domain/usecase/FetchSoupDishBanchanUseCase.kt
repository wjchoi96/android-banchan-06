package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchSoupDishBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsUseCase: FetchCartItemsUseCase
) {
    suspend operator fun invoke(): Result<List<BanchanModel>>{
        return kotlin.runCatching {
            val cart = fetchCartItemsUseCase().getOrThrow()
            listOf(
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
            ) + banchanRepository.fetchSoupDishBanchan().getOrThrow().map {
                if(cart[it.hash] != null)
                    it.copy(isCartItem = true)
                else
                    it
            }
        }
    }
}