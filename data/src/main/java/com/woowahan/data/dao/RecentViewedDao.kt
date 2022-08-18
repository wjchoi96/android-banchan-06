package com.woowahan.data.dao

import androidx.room.*
import com.woowahan.data.entity.dto.CartDto
import com.woowahan.data.entity.dto.RecentViewedEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity

@Dao
interface RecentViewedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentViewed(recentViewedItem: RecentViewedEntity, recentViewedTable: RecentViewedTableEntity)

    @Transaction
    @Query("SELECT * FROM ${RecentViewedTableEntity.TABLE_NAME}")
    fun fetchCartItems(): List<CartDto>
}