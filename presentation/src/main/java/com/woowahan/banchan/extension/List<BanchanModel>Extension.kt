package com.woowahan.banchan.extension

import com.woowahan.domain.model.BanchanModel
import timber.log.Timber

fun List<BanchanModel>.getNewListApplyCartState(banchanModel: BanchanModel, state: Boolean): List<BanchanModel>{
    this.indices.find { this[it].hash == banchanModel.hash }?.let { position ->
        val newList = this.toMutableList().apply {
            this[position] = this[position].copy(isCartItem = state)
        }
        Timber.d("getNewListApplyCartState[$position] => ${newList[position].isCartItem}")
        return newList
    }
    return this.toList()
}

fun List<BanchanModel>.filterType(filterType: BanchanModel.FilterType): List<BanchanModel> {
    return listOf(
        BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
        BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
    ) + this
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