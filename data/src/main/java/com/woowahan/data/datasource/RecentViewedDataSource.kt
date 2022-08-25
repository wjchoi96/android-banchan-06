package com.woowahan.data.datasource

import androidx.paging.PagingData
import com.woowahan.data.entity.dto.RecentViewedEntity
import kotlinx.coroutines.flow.Flow

interface RecentViewedDataSource {
    suspend fun insertRecentViewed(hash: String, title: String, time: String)

    suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>>

    suspend fun fetchRecentViewedPaging(): Flow<PagingData<RecentViewedEntity>>
}