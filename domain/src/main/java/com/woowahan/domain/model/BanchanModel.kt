package com.woowahan.domain.model

data class BanchanModel(
    override val hash: String,
    override val title: String,
    val description: String,
    override val imageUrl: String,
    override val price: Long,
    override val salePrice: Long,
    val viewType: ViewType = ViewType.Item,
    val isCartItem: Boolean = false
): BaseBanchan() {
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
        SalePercentHigher(3, "할인율순");
        companion object {
            fun find(value: Any): FilterType?{
                values().forEach {
                    if(it.title == value || it.value == value)
                        return it
                }
                return null
            }
        }
    }
}