package com.woowahan.domain.model

data class BanchanModel(
    val hash: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val price: String,
    val salePrice: String?,
    val viewType: ViewType = ViewType.Item,
    val isCartItem: Boolean = false
) {
    companion object {
        fun empty(): BanchanModel = BanchanModel("", "", "", "", "", null)
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

    val priceRaw: Long = priceStrToLong(price)

    val salePriceRaw: Long =
        if (salePrice.isNullOrBlank()) 0 else priceStrToLong(salePrice)

    val salePercent: Int
        get() {
            return if (salePriceRaw != 0L) {
                val saleValue = (priceRaw - salePriceRaw).toFloat()
                (saleValue / priceRaw * 100).toInt()
            } else
                0
        }

    private fun priceStrToLong(priceStr: String): Long {
        val temp = priceStr.filter { it.isDigit() }
        return if (temp.isBlank()) 0L else temp.toLong()
    }
}