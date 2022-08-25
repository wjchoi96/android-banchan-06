package com.woowahan.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.woowahan.data.entity.dto.RecentViewedDto
import com.woowahan.data.entity.table.RecentViewedTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentViewedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentViewed(recentViewed: RecentViewedTableEntity)

    @Transaction
    @Query("SELECT * FROM ${RecentViewedTableEntity.TABLE_NAME} ORDER BY ${RecentViewedTableEntity.COLUMN_TIME} DESC")
    fun fetchAllRecentViewedItemsFlow(): Flow<List<RecentViewedDto>>

    @Transaction
    @Query("SELECT * FROM ${RecentViewedTableEntity.TABLE_NAME} ORDER BY ${RecentViewedTableEntity.COLUMN_TIME} DESC")
    fun fetchRecentViewedPaging(): PagingSource<Int, RecentViewedDto>

    @Transaction
    @Query("SELECT * FROM ${RecentViewedTableEntity.TABLE_NAME} ORDER BY ${RecentViewedTableEntity.COLUMN_TIME} DESC LIMIT :count")
    fun fetchSeveralRecentViewedItemsFlow(count: Int): Flow<List<RecentViewedDto>>
}