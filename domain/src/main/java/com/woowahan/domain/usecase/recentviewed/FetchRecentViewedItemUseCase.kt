package com.woowahan.domain.usecase.recentviewed

import com.woowahan.domain.model.RecentViewedItemModel
import com.woowahan.domain.repository.RecentViewedRepository
import kotlinx.coroutines.flow.Flow

class FetchRecentViewedItemUseCase(
    private val recentViewedRepository: RecentViewedRepository,
) {
    suspend operator fun invoke(fetchItemsCnt: Int? = null): Flow<Result<List<RecentViewedItemModel>>> {
        return recentViewedRepository.fetchRecentViewedItems(fetchItemsCnt)
    }
}