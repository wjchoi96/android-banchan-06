package com.woowahan.domain.model

import java.util.*

sealed class OrderItemTypeModel {
    data class Header(
        val deliveryState: Boolean,
        val deliveryStartDate: Date?,
        val currentDate: Date?,
        val deliveryTimeMinute: Int,
        val deliveryCount: Int
    ): OrderItemTypeModel()

    data class Order(
        val orderItem: OrderItemModel
    ): OrderItemTypeModel()

    data class Footer(
        val price: Long,
        val deliveryFee: Long
    ) : OrderItemTypeModel() {
        val totalPrice: Long = price + deliveryFee
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Order(1),
        Footer(2)
    }

    infix fun isSameId(other: Any?): Boolean{
        return when {
            this is Header && other is Header -> true
            this is Order && other is Order -> this.orderItem.hash == other.orderItem.hash
            this is Footer && other is Footer -> true
            else -> this == other
        }
    }

    infix fun isSameContent(other: Any?): Boolean {
        return when {
            this is Header && other is Header -> false // header 는 시간과 관련된 값이기 때문에 무조건 payload 로 유도될 수 있도록 false 처리
            this is Order && other is Order -> this == other
            this is Footer && other is Footer -> this == other
            else -> this == other
        }
    }

}

data class OrderModel(
    val orderId: Long,
    val time: Date?,
    val items: List<OrderItemModel>,
    val deliveryState: Boolean
)

data class OrderItemModel(
    val hash: String,
    val imageUrl: String,
    val title: String,
    val count: Int,
    val price: Long
)