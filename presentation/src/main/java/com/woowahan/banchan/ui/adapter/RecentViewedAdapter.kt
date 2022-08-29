package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemMenuTimeStampBinding
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RecentViewedAdapter(
    private val banchanInsertCartListener: (RecentViewedItemModel, Boolean) -> Unit,
    private val itemClickListener: (String, String) -> Unit
) : RecyclerView.Adapter<RecentViewedAdapter.RecentViewedViewHolder>() {
    private var banchans: List<RecentViewedItemModel> = emptyList()
    private val cartStateChangePayload: String = "changePayload"

    suspend fun updateList(list: List<RecentViewedItemModel>){
        val newList = list.toList()
        withContext(Dispatchers.Default) {
            val diffCallback = RecentViewedPagingDiffUtilCallback(banchans, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchans = newList
                diffRes.dispatchUpdatesTo(this@RecentViewedAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewedViewHolder {
        Timber.d("onCreateViewHolder")
        return RecentViewedViewHolder.from(parent, banchanInsertCartListener, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecentViewedViewHolder, position: Int) {
        Timber.d("onBindViewHolder[$position]")
        holder.bind(banchans[position])
    }

    override fun getItemCount(): Int {
        return banchans.size
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
            when (it) {
                cartStateChangePayload -> {
                    holder.bindCartStateChangePayload(banchans[position])
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
        private val oldList: List<RecentViewedItemModel>,
        private val newList: List<RecentViewedItemModel>,
        private val cartStateChangePayload: Any
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
            return when {
                oldList[oldItemPosition].isCartItem != newList[newItemPosition].isCartItem -> cartStateChangePayload
                else -> super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
    }
}