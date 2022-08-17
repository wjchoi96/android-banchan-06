package com.woowahan.banchan

import androidx.recyclerview.widget.DiffUtil
import com.woowahan.domain.model.CartListItemModel

class CartListModelDiffUtilCallback(
    private val oldList: List<CartListItemModel>,
    private val newList: List<CartListItemModel>,
    private val cartStateChangePayload: Any?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] isSameIdWith newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldList[oldItemPosition] is CartListItemModel.Content && newList[newItemPosition] is CartListItemModel.Content) {
            if ((oldList[oldItemPosition] as CartListItemModel.Content).cart.hash == (newList[newItemPosition] as CartListItemModel.Content).cart.hash) {
                return cartStateChangePayload
            }
        } else if (oldList[oldItemPosition] is CartListItemModel.Footer && newList[newItemPosition] is CartListItemModel.Footer) {
            if ((oldList[oldItemPosition] as CartListItemModel.Footer).price == (newList[newItemPosition] as CartListItemModel.Footer).price) {
                return cartStateChangePayload
            }
        }
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}