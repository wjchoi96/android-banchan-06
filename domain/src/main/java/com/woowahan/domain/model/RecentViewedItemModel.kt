package com.woowahan.domain.model

import java.util.*

data class RecentViewedItemModel(
    val id: Long,
    override val hash: String,
    override val title: String,
    override val imageUrl: String,
    override val price: Long,
    override val salePrice: Long,
    val description: String,
    val time: Date?,
    val isCartItem: Boolean = false
) : BaseBanchan()
