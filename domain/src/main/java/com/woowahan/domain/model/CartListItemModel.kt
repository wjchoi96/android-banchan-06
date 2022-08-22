package com.woowahan.domain.model

sealed class CartListItemModel {
    data class Header(val isAllSelected: Boolean) : CartListItemModel()
    data class Content(val cart: CartModel) : CartListItemModel()
    data class Footer(
        val price: Long,
        val recentViewedItems: List<RecentViewedItemModel>,
        val minimumOrderPrice: Long,
        val deliveryFee: Long,
        val freeDeliveryFeePrice: Long,
        val showPriceInfo: Boolean
    ) : CartListItemModel() {
        val totalPrice: Long = price + deliveryFee
    }

    infix fun isSameIdWith(other: Any?): Boolean {
        return when {
            this is Header && other is Header -> true
            this is Content && other is Content -> this.cart.hash == other.cart.hash
            this is Footer && other is Footer -> true
            else -> false
        }
    }

    infix fun isSameContentWith(other: Any?): Boolean {
        return if (this is Header && other is Header) {
            this.isAllSelected == other.isAllSelected
        } else if (this is Content && other is Content) {
            this.cart == other.cart
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
    val title: String,
    val imageUrl: String,
    val price: Long,
    var isSelected: Boolean = false
) {
    companion object {
        fun empty() = CartModel("", 0, "", "", 0L, false)
    }

    fun isEmpty(): Boolean {
        return (hash.isEmpty() && count == 0 && title.isEmpty() && imageUrl.isEmpty() && price == 0L)
    }

    enum class ViewType(val value: Int) {
        Header(0),
        Content(1),
        Footer(2),
        Empty(3)
    }
}
