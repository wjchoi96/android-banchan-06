package com.woowahan.domain.usecase

import com.woowahan.domain.model.BanchanModel

class FilterBanchanUseCase {
    operator fun invoke(list: List<BanchanModel>, filterType: BanchanModel.FilterType): Result<List<BanchanModel>>{
        return kotlin.runCatching {
            listOf(
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Banner),
                BanchanModel.empty().copy(viewType = BanchanModel.ViewType.Header),
            ) + list
                .filter { it.viewType == BanchanModel.ViewType.Item }
                .sortedBy {
                    when(filterType){
                        BanchanModel.FilterType.PriceHigher -> -it.priceRaw
                        BanchanModel.FilterType.PriceLower -> it.priceRaw
                        BanchanModel.FilterType.SalePercentHigher -> -it.salePercent.toLong()
                        BanchanModel.FilterType.SalePercentLower -> it.salePercent.toLong()
                    }
                }
        }
    }
}