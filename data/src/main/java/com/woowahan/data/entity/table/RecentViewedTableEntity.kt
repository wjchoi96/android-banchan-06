package com.woowahan.data.entity.table

import androidx.room.*

@Entity(
    tableName = RecentViewedTableEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = BanchanItemTableEntity::class,
            parentColumns = [BanchanItemTableEntity.COLUMN_HASH],
            childColumns = [RecentViewedTableEntity.COLUMN_HASH],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecentViewedTableEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_HASH) val hash: String,
    @ColumnInfo(name = COLUMN_TIME) val time: String,
) {
    companion object {
        const val TABLE_NAME = "recent_viewed"
        const val COLUMN_HASH = "hash"
        const val COLUMN_TIME = "time"
    }
}