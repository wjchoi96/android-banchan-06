package com.woowahan.data.datasource

import androidx.paging.PagingData
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface RecentViewedDataSource {
    suspend fun insertRecentViewed(banchan: BanchanModel, time: String)

    suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>>

    suspend fun fetchRecentViewedPaging(): Flow<PagingData<RecentViewedEntity>>
}