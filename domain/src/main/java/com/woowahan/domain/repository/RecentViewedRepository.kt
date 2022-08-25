package com.woowahan.domain.repository

import androidx.paging.PagingData
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RecentViewedRepository {
    suspend fun insertRecentViewedItem(banchan: BanchanModel, time: Date): Flow<Boolean>

    suspend fun fetchRecentViewedItems(fetchItemsCnt: Int?): Flow<List<RecentViewedItemModel>>

    suspend fun fetchRecentViewedPaging(): Flow<PagingData<RecentViewedItemModel>>
}