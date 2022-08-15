package com.woowahan.data.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = OrderTableEntity.TABLE_NAME
)
data class OrderTableEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = OrderTableEntity.COLUMN_ID) val id: Int,
    @ColumnInfo(name = OrderTableEntity.COLUMN_TIME)val time: String
) {
    companion object {
        const val TABLE_NAME = "order"
        const val COLUMN_ID = "id"
        const val COLUMN_TIME = "time"
    }
}