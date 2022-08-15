package com.woowahan.domain.model

data class CartModel(
    val id: Int,
    val banchan: BanchanModel,
    val count: Int,
    val viewType: ViewType = ViewType.Items
) {
    companion object {
        fun empty(): CartModel = CartModel(0, BanchanModel.empty(), 0, ViewType.Items)
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Items(1),
        Footer(2)
    }
}