package com.woowahan.banchan

import androidx.recyclerview.widget.DiffUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.RecentViewedItemModel
import timber.log.Timber

class RecentViewedModelDiffUtilCallback(
    private val oldList: List<RecentViewedItemModel>,
    private val newList: List<RecentViewedItemModel>,
    private val cartStateChangePayload: Any?
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hash == newList[newItemPosition].hash
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if(oldList[oldItemPosition].isCartItem != newList[newItemPosition].isCartItem) {
            return cartStateChangePayload
        }
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}