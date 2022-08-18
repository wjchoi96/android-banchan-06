package com.woowahan.domain.model

import java.util.*

data class RecentViewedItemModel(
    val hash: String,
    val title: String,
    val imageUrl: String,
    val n_price: Long,
    val s_price: Long,
    val time: Date?
)