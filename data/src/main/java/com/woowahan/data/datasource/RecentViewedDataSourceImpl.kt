package com.woowahan.data.datasource

import com.woowahan.data.dao.BanchanDao
import com.woowahan.data.dao.RecentViewedDao
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentViewedDataSourceImpl @Inject constructor(
    private val banchanDao: BanchanDao,
    private val recentViewedDao: RecentViewedDao
) : RecentViewedDataSource {
    override suspend fun insertRecentViewed(
        banchan: BanchanModel,
        time: String
    ) {
        banchanDao.insertBanchanItems(BanchanItemTableEntity(banchan.hash, banchan.title))
        recentViewedDao.insertRecentViewed(RecentViewedTableEntity(banchan.hash, time))
    }

    override suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>> {
        return (if (fetchItemsCnt == null) {
            recentViewedDao.fetchAllRecentViewedItemsFlow()
        } else {
            recentViewedDao.fetchSeveralRecentViewedItemsFlow(fetchItemsCnt)
        }).map { it.map { dto -> dto.toEntity() } }
    }

}