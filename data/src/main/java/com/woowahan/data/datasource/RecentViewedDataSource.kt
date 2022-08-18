package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.RecentViewedDto
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow

interface RecentViewedDataSource {
    suspend fun insertRecentViewed(banchan: BanchanModel, time: String)

    suspend fun fetchRecentViewedFlow(): Flow<List<RecentViewedEntity>>
}