package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.BanchanRepository

class FetchSoupDishBanchanUseCase(
    private val banchanRepository: BanchanRepository
) {
    suspend operator fun invoke(): Result<List<BanchanModel>>{
        return kotlin.runCatching {
            listOf(
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
            ) + banchanRepository.fetchSoupDishBanchan().getOrThrow()
        }
    }
}