package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.flow.Flow

interface RecentViewedRepository {
    suspend fun insertRecentViewedItem(banchan: BanchanModel, time: String): Flow<Result<Boolean>>

    suspend fun fetchRecentViewedItems(): Flow<Result<List<RecentViewedItemModel>>>
}