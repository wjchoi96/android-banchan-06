package com.woowahan.banchan

import androidx.recyclerview.widget.DiffUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.CartModel
import timber.log.Timber

class CartModelDiffUtilCallback(
    private val oldList: List<CartModel>,
    private val newList: List<CartModel>,
    private val cartStateChangePayload: Any?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldList[oldItemPosition].count != newList[newItemPosition].count) {
            return cartStateChangePayload
        }
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}