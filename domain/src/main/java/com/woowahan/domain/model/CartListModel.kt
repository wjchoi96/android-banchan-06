package com.woowahan.domain.model

sealed class CartListModel {
    object Header: CartListModel()
    data class Content()
}

data class CartModel(
    val hash: String,
    val count: Int,
    val viewType: ViewType = ViewType.Items,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val deliveryFee: Long,
    val isSelected: Boolean = false
) {
    companion object {
        fun header(): CartModel = CartModel.empty().copy(viewType = ViewType.Header)
        fun empty(): CartModel = CartModel("", 0, ViewType.Items, "", "", 0L, 0L)
        fun footer(): CartModel = CartModel.empty().copy(viewType = ViewType.Footer)
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Items(1),
        Footer(2)
    }
}
