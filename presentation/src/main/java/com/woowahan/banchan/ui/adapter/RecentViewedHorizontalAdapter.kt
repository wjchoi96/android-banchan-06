package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.RecentViewedModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemMenuSmallBinding
import com.woowahan.domain.model.RecentViewedItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentViewedHorizontalAdapter(
    private val itemClickListener: (String, String) -> Unit
) :
    RecyclerView.Adapter<RecentViewedHorizontalAdapter.RecentViewedHorizontalViewHolder>() {
    private var banchanList = listOf<RecentViewedItemModel>()

    fun updateList(newList: List<RecentViewedItemModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                RecentViewedModelDiffUtilCallback(banchanList, newList, "Not need payload")
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList = newList.toList()
                diffRes.dispatchUpdatesTo(this@RecentViewedHorizontalAdapter)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentViewedHorizontalViewHolder {
        return RecentViewedHorizontalViewHolder.from(
            parent,
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecentViewedHorizontalViewHolder, position: Int) {
        holder.bind(banchanList[position])
    }

    override fun getItemCount(): Int = banchanList.size

    class RecentViewedHorizontalViewHolder(
        private val binding: ItemMenuSmallBinding,
        val itemClickListener: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: (String, String) -> Unit
            ): RecentViewedHorizontalViewHolder =
                RecentViewedHorizontalViewHolder(
                    ItemMenuSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    itemClickListener
                )
        }

        fun bind(item: RecentViewedItemModel) {
            binding.banchan = item
            binding.holder = this
        }
    }
}