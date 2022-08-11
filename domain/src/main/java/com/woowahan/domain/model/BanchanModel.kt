package com.woowahan.domain.model

data class BanchanModel(
    val hash: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val price: String,
    val salePrice: String?,
    val viewType: ViewType = ViewType.Item
) {
    companion object {
        fun empty(): BanchanModel = BanchanModel("", "", "", "", "", null)
    }

    enum class ViewType(val value: Int) {
        Banner(0),
        Header(1),
        Item(2)
    }

    enum class FilterType(val value: Int) {
        Default(0),
        PriceHigher(1),
        PriceLower(2),
        SalePercentHigher(3),
        SalePercentLower(4)
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