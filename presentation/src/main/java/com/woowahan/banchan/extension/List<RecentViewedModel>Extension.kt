package com.woowahan.banchan.extension

import com.woowahan.domain.model.RecentViewedItemModel
import timber.log.Timber

fun List<RecentViewedItemModel>.getNewListApplyCartState(banchan: RecentViewedItemModel, state: Boolean): List<RecentViewedItemModel>{
    this.indices.find { this[it].hash == banchan.hash }?.let { position ->
        val newList = this.toMutableList().apply {
            this[position] = this[position].copy(isCartItem = state)
        }
        Timber.d("getNewListApplyCartState[$position] => ${newList[position].isCartItem}")
        return newList
    }
    return this.toList()
}