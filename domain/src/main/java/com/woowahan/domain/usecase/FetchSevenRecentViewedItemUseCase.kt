package com.woowahan.domain.usecase

import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow

class FetchSevenRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(): Flow<Result<List<RecentViewedItemModel>>> {
        return recentViewedRepository.fetchRecentViewedItems(fetchItemsCnt = 7)
    }
}