package com.woowahan.data.datasource

import com.woowahan.data.dao.CartDao
import com.woowahan.data.dao.RecentViewedDao
import com.woowahan.data.entity.dto.CartDto
import com.woowahan.data.entity.dto.RecentViewedDto
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentViewedDataSourceImpl @Inject constructor(
    private val recentViewedDao: RecentViewedDao
) : RecentViewedDataSource {
    override suspend fun insertRecentViewed(
        banchan: BanchanModel,
        time: String
    ) {
        recentViewedDao.insertRecentViewed(
            BanchanItemTableEntity(banchan.hash, banchan.title),
            RecentViewedTableEntity(banchan.hash, time)
        )
    }

    override suspend fun fetchRecentViewedFlow(): Flow<List<RecentViewedEntity>> {
        return recentViewedDao.fetchRecentViewedItemsFlow().map { it.map { dto -> dto.toEntity() } }
    }

}