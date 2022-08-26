package com.woowahan.data.dao

import com.woowahan.data.entity.dto.RecentViewedDto
import com.woowahan.data.entity.table.RecentViewedTableEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpyRecentViewedDaoImpl(

) : RecentViewedDao {
    var insertCalledCount: Int = 0
        private set

    override fun insertRecentViewed(recentViewed: RecentViewedTableEntity) {
        insertCalledCount++
    }

    override fun fetchAllRecentViewedItemsFlow(): Flow<List<RecentViewedDto>> = flow {
        emit()
    }

    override fun fetchSeveralRecentViewedItemsFlow(count: Int): Flow<List<RecentViewedDto>> {
        TODO("Not yet implemented")
    }

}