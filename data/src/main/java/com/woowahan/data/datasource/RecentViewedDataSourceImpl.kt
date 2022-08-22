package com.woowahan.data.datasource

import com.woowahan.data.dao.BanchanDao
import com.woowahan.data.dao.RecentViewedDao
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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

    override suspend fun fetchRecentViewedFlow(fetchItemsCnt: Int?): Flow<List<RecentViewedEntity>> = flow {
        if (fetchItemsCnt == null) {
            recentViewedDao.fetchAllRecentViewedItemsFlow()
        } else {
            recentViewedDao.fetchSeveralRecentViewedItemsFlow(fetchItemsCnt)
        }.collect {
            emit(it.map { dto -> dto.toEntity() })
        }
    }

}