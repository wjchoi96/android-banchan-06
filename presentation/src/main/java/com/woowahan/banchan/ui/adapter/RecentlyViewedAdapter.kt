package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemMenuTimeStampBinding
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RecentlyViewedAdapter(
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> Unit,
    private val itemClickListener: (BanchanModel) -> Unit
) : RecyclerView.Adapter<RecentlyViewedAdapter.RecentlyViewedViewHolder>() {

    private var banchanList = listOf<BanchanModel>()

    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<BanchanModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                BanchanModelDiffUtilCallback(banchanList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList = newList.toList()
                diffRes.dispatchUpdatesTo(this@RecentlyViewedAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyViewedViewHolder {
        Timber.d("onCreateViewHolder")
        return RecentlyViewedViewHolder.from(parent, banchanInsertCartListener, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecentlyViewedViewHolder, position: Int) {
        Timber.d("onBindViewHolder[$position]")
        holder.bind(banchanList[position])
    }

    override fun onBindViewHolder(
        holder: RecentlyViewedViewHolder,
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
                    holder.bindCartStateChangePayload(banchanList[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = banchanList.size

    class RecentlyViewedViewHolder(
        private val binding: ItemMenuTimeStampBinding,
        val banchanInsertCartListener: (BanchanModel, Boolean) -> Unit,
        val itemClickListener: (BanchanModel) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                parent: ViewGroup,
                banchanInsertCartListener: (BanchanModel, Boolean) -> Unit,
                itemClickListener: (BanchanModel) -> Unit
            ): RecentlyViewedViewHolder = RecentlyViewedViewHolder(
                ItemMenuTimeStampBinding.inflate(LayoutInflater.from(parent.context)),
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
}