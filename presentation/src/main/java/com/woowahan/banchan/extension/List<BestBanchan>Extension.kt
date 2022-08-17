package com.woowahan.banchan.extension

import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import timber.log.Timber

fun List<BestBanchanModel>.getNewListApplyCartState(banchanModel: BanchanModel, state: Boolean): List<BestBanchanModel>{

    var newBestBanchan: BestBanchanModel? = null
    var newBestBanchanPosition: Int = -1

    this.forEachIndexed { i, it ->
        it.banchans.indices.find { position -> it.banchans[position].hash == banchanModel.hash }?.let { position ->
            val newList = it.banchans.toMutableList().apply {
                this[position] = it.banchans[position].copy(isCartItem = state)
            }
            newBestBanchan = it.copy(banchans = newList)
            newBestBanchanPosition = i
        }
    }
    if(newBestBanchanPosition in 0 until this.size){
        newBestBanchan?.let {
            Timber.d("getNewListApplyCartState[$newBestBanchanPosition]")
            return this.toMutableList().apply {
                this[newBestBanchanPosition] = it
            }
        }
    }

    return this.toList()
}