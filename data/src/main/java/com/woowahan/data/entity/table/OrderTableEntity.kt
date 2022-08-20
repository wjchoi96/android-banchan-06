package com.woowahan.data.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = OrderTableEntity.TABLE_NAME
)
data class OrderTableEntity(
    @ColumnInfo(name = OrderTableEntity.COLUMN_TIME)val time: String,
    @ColumnInfo(name = OrderTableEntity.COLUMN_STATE)val deliveryState: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = OrderTableEntity.COLUMN_ID) var id: Long = 0
    companion object {
        const val TABLE_NAME = "order"
        const val COLUMN_ID = "id"
        const val COLUMN_TIME = "time"
        const val COLUMN_STATE = "delivery_state"
    }
}