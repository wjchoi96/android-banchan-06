package com.woowahan.domain.model

data class BanchanDetailModel(
    val hash: String,
    val title: String,
    val imageUrl: String,
    val price: Long,
    val salePrice: Long,
    val deliveryFee: Long,
    val freeDeliveryFeePrice: Long,
    val description: String,
    val point: Long,
    val deliveryInfo: String,
    val deliveryFeeInfo: String,
    val detailImages: List<String>,
    val isCartItem: Boolean,
    val thumbImages: List<String>
) {
    companion object {
        fun empty() =
            BanchanDetailModel(
                "",
                "",
                "",
                0L,
                0L,
                0L,
                0L,
                "",
                0L,
                "",
                "",
                emptyList(),
                false,
                emptyList()
            )
    }

    val salePercent: Int
        get() {
            return if (salePrice != 0L) {
                val saleValue = (price - salePrice).toFloat()
                (saleValue / price * 100).toInt()
            } else
                0
        }

    fun isNotEmpty(): Boolean {
        return this != empty()
    }
}