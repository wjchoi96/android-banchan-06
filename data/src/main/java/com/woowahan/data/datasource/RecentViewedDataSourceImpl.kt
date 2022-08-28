package com.woowahan.data.datasource

import com.woowahan.data.dao.BanchanDao
import com.woowahan.data.dao.RecentViewedDao
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecentViewedDataSourceImpl @Inject constructor(
    private val banchanDao: BanchanDao,
    private val recentViewedDao: RecentViewedDao
) : RecentViewedDataSource {
    override suspend fun insertRecentViewed(
        hash: String,
        title: String,
        time: String
    ) {
        banchanDao.insertBanchanItems(BanchanItemTableEntity(hash, title))
        recentViewedDao.insertRecentViewed(RecentViewedTableEntity(hash, time))
    }

    override suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>> =
        flow {
            if (fetchItemsCnt == null) {
                recentViewedDao.fetchAllRecentViewedItemsFlow()
            } else {
                recentViewedDao.fetchSeveralRecentViewedItemsFlow(fetchItemsCnt)
            }.collect {
                emit(it.map { dto -> dto.toEntity() })
            }
        }
}