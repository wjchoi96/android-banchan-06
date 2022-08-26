package com.woowahan.data.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = BanchanItemTableEntity.TABLE_NAME
)
data class BanchanItemTableEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_HASH)val hash: String,
    @ColumnInfo(name = COLUMN_TITLE)val title: String
) {
    companion object {
        const val TABLE_NAME = "banchan_item"
        const val COLUMN_HASH = "hash"
        const val COLUMN_TITLE = "title"
    }
}