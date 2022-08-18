package com.woowahan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woowahan.data.entity.table.BanchanItemTableEntity

@Dao
interface BanchanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBanchanItems(banchan: BanchanItemTableEntity)

    @Query("DELETE FROM ${BanchanItemTableEntity.TABLE_NAME} WHERE ${BanchanItemTableEntity.COLUMN_HASH} in (:hash)")
    fun removeBanchanItems(vararg hash: String): Int
}