package com.woowahan.banchan.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemMenuHorizontalBinding
import com.woowahan.domain.model.BanchanModel
import timber.log.Timber

class MenuHorizontalViewHolder(
    private val binding: ItemMenuHorizontalBinding,
    val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(
            parent: ViewGroup,
            banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
        ): MenuHorizontalViewHolder =
            MenuHorizontalViewHolder(
                ItemMenuHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                banchanInsertCartListener
            )
    }

    fun bind(item: BanchanModel) {
        Timber.d("bind called")
        binding.banchan = item
        binding.isCartItem = item.isCartItem
        binding.holder = this
    }

    fun bindCartStateChangePayload(item: BanchanModel) {
        Timber.d("bindPayload bindCartStateChangePayload")
        binding.isCartItem = item.isCartItem
    }
}