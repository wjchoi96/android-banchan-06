package com.woowahan.domain.model

import java.util.*

data class RecentViewedItemModel(
    override val hash: String,
    override val title: String,
    override val imageUrl: String,
    override val price: Long,
    override val salePrice: Long,
    val time: Date?
): BaseBanchan()