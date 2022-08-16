package com.woowahan.banchan

import androidx.recyclerview.widget.DiffUtil
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.CartListModel
import com.woowahan.domain.model.CartModel
import timber.log.Timber

class CartListModelDiffUtilCallback(
    private val oldList: List<CartListModel>,
    private val newList: List<CartListModel>,
    private val cartStateChangePayload: Any?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldList[oldItemPosition] is CartListModel.Header && newList[newItemPosition] is CartListModel.Header) {
            true
        } else if (oldList[oldItemPosition] is CartListModel.Content && newList[newItemPosition] is CartListModel.Content) {
            (oldList[oldItemPosition] as CartListModel.Content).cart.hash == (newList[newItemPosition] as CartListModel.Content).cart.hash
        } else if (oldList[oldItemPosition] is CartListModel.Footer && newList[newItemPosition] is CartListModel.Footer) {
            (oldList[oldItemPosition] as CartListModel.Footer).price == (newList[newItemPosition] as CartListModel.Footer).price
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldList[oldItemPosition] is CartListModel.Content && newList[newItemPosition] is CartListModel.Content) {
            if ((oldList[oldItemPosition] as CartListModel.Content).cart.hash == (newList[newItemPosition] as CartListModel.Content).cart.hash) {
                return cartStateChangePayload
            }
        } else if (oldList[oldItemPosition] is CartListModel.Footer && newList[newItemPosition] is CartListModel.Footer) {
            if ((oldList[oldItemPosition] as CartListModel.Footer).price == (newList[newItemPosition] as CartListModel.Footer).price) {
                return cartStateChangePayload
            }
        }
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}