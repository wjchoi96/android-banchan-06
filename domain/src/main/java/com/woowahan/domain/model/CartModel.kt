package com.woowahan.domain.model

data class CartModel(
    val id: Int,
    val banchan: BanchanModel,
    val count: Int,
) {
    companion object {
        fun empty(): CartModel = CartModel(0, BanchanModel.empty(), 0)
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Items(1),
        Footer(2)
    }
}