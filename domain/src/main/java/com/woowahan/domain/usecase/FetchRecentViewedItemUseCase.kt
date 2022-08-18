package com.woowahan.domain.usecase

import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(): Flow<Result<List<RecentViewedItemModel>>> {
        return recentViewedRepository.fetchRecentViewedItems()
    }
}