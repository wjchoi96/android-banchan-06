package com.woowahan.banchan.util

import com.woowahan.domain.model.BanchanModel

object FilterBanchanListUtil {
    fun filter(
        list: List<BanchanModel>,
        filterType: BanchanModel.FilterType
    ): List<BanchanModel> {
        return listOf(
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
            BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
        ) + list
            .filter { it.viewType == BanchanModel.ViewType.Item }
            .sortedBy {
                when (filterType) {
                    BanchanModel.FilterType.PriceHigher -> if (it.salePercent == 0) -it.priceRaw else -it.salePriceRaw
                    BanchanModel.FilterType.PriceLower -> if (it.salePercent == 0) it.priceRaw else it.salePriceRaw
                    BanchanModel.FilterType.SalePercentHigher -> -it.salePercent.toLong()
                    else -> throw Throwable("Unknown filter item")
                }
            }
    }
}
