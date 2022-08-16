package com.woowahan.domain.model

sealed class CartListModel {
    object Header: CartListModel()
    data class Content(val cart: CartModel): CartListModel()
    data class Footer(val price: Long, val deliveryFee: Long, val totalPrice: Long): CartListModel()
}

data class CartModel(
    val hash: String,
    val count: Int,
    val viewType: ViewType = ViewType.Content,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val deliveryFee: Long = 2500L,
    val isSelected: Boolean = false
) {
    companion object {
        fun empty(): CartModel = CartModel("", 0, ViewType.Content, "", "", 0L, 0L)
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Content(1),
        Footer(2)
    }
}
