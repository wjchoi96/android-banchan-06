package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface RecentViewedDataSource {
    suspend fun insertRecentViewed(hash: String, title: String, time: String)

    suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>>
}