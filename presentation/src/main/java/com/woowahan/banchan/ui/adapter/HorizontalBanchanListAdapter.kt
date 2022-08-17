package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemMenuBestHorizontalChildBinding
import com.woowahan.banchan.ui.adapter.viewHolder.MenuHorizontalViewHolder
import com.woowahan.banchan.ui.adapter.viewHolder.MenuVerticalViewHolder
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HorizontalBanchanListAdapter(
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> Unit,
    private val itemClickListener: (BanchanModel) -> Unit
): RecyclerView.Adapter<HorizontalBanchanListAdapter.BestMenuHorizontalChildViewHolder>() {

    private var banchanList = listOf<BanchanModel>()
    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<BanchanModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                BanchanModelDiffUtilCallback(banchanList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList = newList.toList()
                diffRes.dispatchUpdatesTo(this@HorizontalBanchanListAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestMenuHorizontalChildViewHolder {
        return BestMenuHorizontalChildViewHolder.from(
            parent,
            banchanInsertCartListener,
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: BestMenuHorizontalChildViewHolder, position: Int) {
        holder.bind(banchanList[position])
    }

    override fun onBindViewHolder(
        holder: BestMenuHorizontalChildViewHolder,
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

    class BestMenuHorizontalChildViewHolder(
        private val binding: ItemMenuBestHorizontalChildBinding,
        val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit),
        val itemClickListener: (BanchanModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit),
                itemClickListener: (BanchanModel) -> Unit
            ): BestMenuHorizontalChildViewHolder =
                BestMenuHorizontalChildViewHolder(
                    ItemMenuBestHorizontalChildBinding.inflate(
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


}