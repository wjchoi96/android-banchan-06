package com.woowahan.domain.model

sealed class CartListModel {
    data class Header(val isAllSelected: Boolean) : CartListModel()
    data class Content(val cart: CartModel) : CartListModel()
    data class Footer(val price: Long, val deliveryFee: Long, val totalPrice: Long) :
        CartListModel()

    fun isSameHash(other: Any?): Boolean {
        return if (this is Header && other is Header) {
            this.isAllSelected == other.isAllSelected
        } else if (this is Content && other is Content) {
            this.cart.hash == other.cart.hash
        } else if (this is Footer && other is Footer) {
            this.price == other.price
        } else {
            false
        }
    }
}

data class CartModel(
    val hash: String,
    val count: Int,
    val viewType: ViewType = ViewType.Content,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val deliveryFee: Long = 2500L,
    var isSelected: Boolean = false
) {
    companion object {
        fun empty(): CartModel = CartModel("", 0, ViewType.Content, "", "", 0L, 0L)
    }

    fun isSameHash(other: Any?): Boolean {
        return if (other is CartModel) {
            this.hash == other.hash
        } else {
            false
        }
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Content(1),
        Footer(2)
    }
}
