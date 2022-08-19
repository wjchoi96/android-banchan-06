package com.woowahan.domain.model

import java.util.*

sealed class OrderItemTypeModel {
    data class Header(
        val deliveryTime: String,
        val deliveryCount: Int
    ): OrderItemTypeModel()

    data class Order(
        val orderItem: OrderItemModel
    ): OrderItemTypeModel()

    data class Footer(
        val price: Long,
        val deliveryFee: Long = 2500L,
    ) : OrderItemTypeModel() {
        val totalPrice: Long = price + deliveryFee
    }

}

data class OrderModel(
    val orderId: Long,
    val time: Date?,
    val items: List<OrderItemModel>
)

data class OrderItemModel(
    val hash: String,
    val imageUrl: String,
    val title: String,
    val count: Int,
    val price: Long
)