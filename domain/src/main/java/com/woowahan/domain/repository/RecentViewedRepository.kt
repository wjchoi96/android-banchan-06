package com.woowahan.domain.repository

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RecentViewedRepository {
    suspend fun insertRecentViewedItem(hash: String, title: String, time: Date): Flow<Boolean>

    suspend fun fetchRecentViewedItems(fetchItemsCnt: Int?): Flow<List<RecentViewedItemModel>>
}