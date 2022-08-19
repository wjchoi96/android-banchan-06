package com.woowahan.data.entity.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.woowahan.domain.model.OrderItemModel

@Entity(
    tableName = OrderItemTableEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = OrderTableEntity::class,
            parentColumns = [OrderTableEntity.COLUMN_ID],
            childColumns = [OrderItemTableEntity.COLUMN_ORDER_ID],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class OrderItemTableEntity(
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_ORDER_ID) val orderId: Long,
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_HASH) val hash: String,
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_IMAGE_URL) val imageUrl: String,
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_TITLE) val title: String,
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_COUNT) val count: Int,
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_PRICE) val price: Long,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = OrderItemTableEntity.COLUMN_ID) var id: Long = 0
    companion object {
        const val TABLE_NAME = "order_item"
        const val COLUMN_ID = "id"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_HASH = "hash"
        const val COLUMN_IMAGE_URL = "image_url"
        const val COLUMN_TITLE = "title"
        const val COLUMN_COUNT = "count"
        const val COLUMN_PRICE = "price"
    }

    fun toDomain(): OrderItemModel = OrderItemModel(
        hash = hash,
        imageUrl = imageUrl,
        title = title,
        count = count,
        price = price
    )
}
