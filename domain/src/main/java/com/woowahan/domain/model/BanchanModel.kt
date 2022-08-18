package com.woowahan.domain.model

data class BanchanModel(
    val hash: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val price: Long,
    val salePrice: Long,
    val viewType: ViewType = ViewType.Item,
    val isCartItem: Boolean = false
) {
    companion object {
        fun empty(): BanchanModel = BanchanModel("", "", "", "", 0L, 0L)
        fun getFilterList(): List<String> {
            return FilterType.values().map { it.title }
        }
    }

    enum class ViewType(val value: Int) {
        Banner(0),
        Header(1),
        Item(2)
    }

    enum class FilterType(val value: Int, val title: String) {
        Default(0, "기본 정렬순"),
        PriceHigher(1, "금액 높은순"),
        PriceLower(2, "금액 낮은순"),
        SalePercentHigher(3, "할인율순")
    }

    val salePercent: Int
        get() {
            return if (salePrice != 0L) {
                val saleValue = (price - salePrice).toFloat()
                (saleValue / price * 100).toInt()
            } else
                0
        }
}