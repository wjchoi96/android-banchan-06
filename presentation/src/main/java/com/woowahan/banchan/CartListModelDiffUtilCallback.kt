package com.woowahan.banchan

import androidx.recyclerview.widget.DiffUtil
import com.woowahan.banchan.ui.adapter.DefaultCartAdapter
import com.woowahan.domain.model.CartListItemModel
import timber.log.Timber

class CartListModelDiffUtilCallback(
    private val oldList: List<CartListItemModel>,
    private val newList: List<CartListItemModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] isSameIdWith newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] isSameContentWith newList[newItemPosition]
    }
    
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        when {
            oldItem is CartListItemModel.Header && newItem is CartListItemModel.Header -> {
                if (!(oldItem isSameContentWith newItem)) {
                    return DefaultCartAdapter.Payload.SelectAllChanged
                }
            }
            oldItem is CartListItemModel.Content && newItem is CartListItemModel.Content -> {
                if (oldItem.cart.isSelected != newItem.cart.isSelected) {
                    return DefaultCartAdapter.Payload.SelectOneChanged
                }
                if (oldItem.cart.count != newItem.cart.count) {
                    return DefaultCartAdapter.Payload.quantityChanged
                }
            }
            oldItem is CartListItemModel.Footer && newItem is CartListItemModel.Footer -> {
                if (!(oldItem isSameContentWith newItem)) {
                    return DefaultCartAdapter.Payload.totalPriceChanged
                }
            }
        }

        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}