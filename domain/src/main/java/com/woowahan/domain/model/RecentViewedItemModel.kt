package com.woowahan.domain.model

import com.woowahan.domain.util.BanchanDateConvertUtil

data class RecentViewedItemModel(
    val hash: String,
    val title: String,
    val imageUrl: String,
    val n_price: Long,
    val s_price: Long,
    val timeStr: String
){
    val time = BanchanDateConvertUtil.convert(timeStr)
}