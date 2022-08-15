package com.woowahan.domain.model

data class CartModel(
    val hash: String,
    val count: Int,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val deliveryFee: Long
)
