package com.woowahan.data.dao

import androidx.room.*
import com.woowahan.data.entity.dto.RecentViewedDto
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentViewedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentViewed(banchan: BanchanItemTableEntity, recentViewed: RecentViewedTableEntity)

    @Transaction
    @Query("SELECT * FROM ${RecentViewedTableEntity.TABLE_NAME}")
    fun fetchRecentViewedItemsFlow(): Flow<List<RecentViewedDto>>
}