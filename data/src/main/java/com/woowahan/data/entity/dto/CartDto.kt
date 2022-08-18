package com.woowahan.data.entity.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity

data class CartDto(
    @Embedded
    val cart: CartTableEntity,
    @Relation(
        parentColumn = BanchanItemTableEntity.COLUMN_HASH,
        entityColumn = CartTableEntity.COLUMN_HASH
    )
    var cartItemInfo: BanchanItemTableEntity
) {
    fun toEntity(): CartEntity = CartEntity(
        cartItemInfo.hash,
        cart.count,
        cart.isSelect,
        cartItemInfo.title
    )
}

data class CartEntity(
    val hash: String,
    val count: Int,
    val isSelect: Boolean,
    val title: String
)