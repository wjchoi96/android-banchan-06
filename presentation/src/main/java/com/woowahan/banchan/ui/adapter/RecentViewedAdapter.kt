package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.RecentViewedModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemMenuTimeStampBinding
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RecentViewedAdapter(
    private val banchanInsertCartListener: (RecentViewedItemModel, Boolean) -> Unit,
    private val itemClickListener: (String, String) -> Unit,
    private val cartStateChangePayload: String = "changePayload"
) : PagingDataAdapter<RecentViewedItemModel, RecentViewedAdapter.RecentViewedViewHolder>(
    RecentViewedPagingDiffUtilCallback(cartStateChangePayload = cartStateChangePayload)
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewedViewHolder {
        Timber.d("onCreateViewHolder")
        return RecentViewedViewHolder.from(parent, banchanInsertCartListener, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecentViewedViewHolder, position: Int) {
        Timber.d("onBindViewHolder[$position]")
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onBindViewHolder(
        holder: RecentViewedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        payloads.firstOrNull()?.let {
            Timber.d("onBindViewHolder payloads[$position][$it]")
            getItem(position)?.let { item->
                when (it) {
                    cartStateChangePayload -> {
                        holder.bindCartStateChangePayload(item)
                    }
                }
            }
        }
    }

    class RecentViewedViewHolder(
        private val binding: ItemMenuTimeStampBinding,
        val banchanInsertCartListener: (RecentViewedItemModel, Boolean) -> Unit,
        val itemClickListener: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                parent: ViewGroup,
                banchanInsertCartListener: (RecentViewedItemModel, Boolean) -> Unit,
                itemClickListener: (String, String) -> Unit
            ): RecentViewedViewHolder = RecentViewedViewHolder(
                ItemMenuTimeStampBinding.inflate(LayoutInflater.from(parent.context)),
                banchanInsertCartListener,
                itemClickListener
            )
        }

        fun bind(item: RecentViewedItemModel) {
            binding.banchan = item
            binding.isCartItem = item.isCartItem
            binding.holder = this
        }

        fun bindCartStateChangePayload(item: RecentViewedItemModel) {
            Timber.d("bindPayload bindCartStateChangePayload")
            binding.isCartItem = item.isCartItem
        }
    }

    class RecentViewedPagingDiffUtilCallback(
        private val cartStateChangePayload: Any
    ) : DiffUtil.ItemCallback<RecentViewedItemModel>() {
        override fun areItemsTheSame(
            oldItem: RecentViewedItemModel,
            newItem: RecentViewedItemModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RecentViewedItemModel,
            newItem: RecentViewedItemModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: RecentViewedItemModel,
            newItem: RecentViewedItemModel
        ): Any? {
            return when {
                oldItem.isCartItem != newItem.isCartItem -> cartStateChangePayload
                else -> super.getChangePayload(oldItem, newItem)
            }
        }

    }
}