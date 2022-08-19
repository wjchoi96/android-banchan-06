package com.woowahan.banchan.extension

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BaseBanchan
import com.woowahan.domain.model.RecentViewedItemModel
import timber.log.Timber

fun List<RecentViewedItemModel>.getNewListApplyCartState(
    banchanModel: BaseBanchan,
    state: Boolean
): List<RecentViewedItemModel> {
    this.indices.find { this[it].hash == banchanModel.hash }?.let { position ->
        val newList = this.toMutableList().apply {
            this[position] = this[position].copy(isCartItem = state)
        }
        Timber.d("getNewListApplyCartState[$position] => ${newList[position].isCartItem}")
        return newList
    }
    return this.toList()
}
