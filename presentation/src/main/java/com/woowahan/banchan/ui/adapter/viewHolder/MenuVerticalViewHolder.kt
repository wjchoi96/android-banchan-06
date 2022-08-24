package com.woowahan.banchan.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemMenuVerticalBinding
import com.woowahan.domain.model.BanchanModel
import timber.log.Timber

class MenuVerticalViewHolder(
    private val binding: ItemMenuVerticalBinding,
    val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit),
    val itemClickListener: (BanchanModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(
            parent: ViewGroup,
            banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit),
            itemClickListener: (BanchanModel) -> Unit
        ): MenuVerticalViewHolder =
            MenuVerticalViewHolder(
                ItemMenuVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                banchanInsertCartListener,
                itemClickListener
            )
    }

    fun bind(item: BanchanModel) {
        binding.banchan = item
        binding.isCartItem = item.isCartItem
        binding.holder = this
    }

    fun bindCartStateChangePayload(item: BanchanModel) {
        Timber.d("bindPayload bindCartStateChangePayload")
        binding.isCartItem = item.isCartItem
    }
}