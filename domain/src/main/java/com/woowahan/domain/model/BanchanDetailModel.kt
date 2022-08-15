package com.woowahan.domain.model

data class BanchanDetailModel(
    val hash: String,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val deliveryFee: Long
) {
}