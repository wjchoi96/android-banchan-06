package com.woowahan.domain.usecase

import com.woowahan.domain.model.BestBanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchBestBanchanUseCase(
    private val banchanRepository: BanchanRepository,
    private val fetchCartItemsKeyUseCase: FetchCartItemsKeyUseCase
){
    suspend operator fun invoke(): Result<List<BestBanchanModel>>{
        return kotlin.runCatching {
            val cart = fetchCartItemsKeyUseCase().getOrThrow()
            listOf(
                BestBanchanModel.empty().copy(viewType = BestBanchanModel.ViewType.Banner)
            ) + banchanRepository.fetchBestBanchan().getOrThrow().map {
                it.banchans.map { banchan ->
                    if(cart.contains(banchan.hash))
                        banchan.copy(isCartItem = true)
                    else
                        banchan
                }
                it
            }
        }
    }
}