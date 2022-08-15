package com.woowahan.data.entity.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.RecentViewedTableEntity

data class RecentViewedDto(
    @Embedded
    val recentViewed: RecentViewedTableEntity,
    @Relation(
        parentColumn = BanchanItemTableEntity.COLUMN_HASH,
        entityColumn = RecentViewedTableEntity.COLUMN_HASH
    )
    var recentViewedItemInfo: BanchanItemTableEntity
) {
    fun toEntity(): RecentViewedEntity = RecentViewedEntity(
        recentViewedItemInfo.hash,
        recentViewed.time,
        recentViewedItemInfo.title
    )
}

data class RecentViewedEntity(
    val hash: String,
    val time: String,
    val title: String
)