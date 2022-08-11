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
                    BanchanModel.FilterType.PriceHigher -> -it.priceRaw
                    BanchanModel.FilterType.PriceLower -> it.priceRaw
                    BanchanModel.FilterType.SalePercentHigher -> -it.salePercent.toLong()
                    else -> throw Throwable("Unknown filter item")
                }
            }
    }
}
