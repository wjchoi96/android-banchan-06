package com.woowahan.data.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.RESTRICT
import androidx.room.PrimaryKey

@Entity(
    tableName = CartTableEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = BanchanItemTableEntity::class,
            parentColumns = [BanchanItemTableEntity.COLUMN_HASH],
            childColumns = [CartTableEntity.COLUMN_HASH],
            onDelete = RESTRICT,
            onUpdate = CASCADE
        )
    ]
)
data class CartTableEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_HASH) val hash: String,
    @ColumnInfo(name = COLUMN_COUNT) val count: Int,
    @ColumnInfo(name = COLUMN_SELECT) val isSelect: Boolean = true
) {
    companion object {
        const val TABLE_NAME = "cart"
        const val COLUMN_HASH = "hash"
        const val COLUMN_COUNT = "count"
        const val COLUMN_SELECT = "isSelect"
    }
}
