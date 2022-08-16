package com.woowahan.banchan.ui.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemMenuHorizontalListBinding
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.ui.adapter.viewHolder.BanchanListBannerViewHolder
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.BestBanchanModel
import timber.log.Timber

class BestBanchanAdapter(
    private val bannerTitle: String,
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var bestBanchans = listOf<BestBanchanModel>()
    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<BestBanchanModel>){
        bestBanchans = newList.toList()
        //TODO: DiffUtil 생성하여 적용, payload 호출 되는지 확인
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            BestBanchanModel.ViewType.Banner.value -> BanchanListBannerViewHolder.from(parent)
            else -> HorizontalListViewHolder.from(parent, banchanInsertCartListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is BanchanListBannerViewHolder -> holder.bind(bannerTitle, true)
            is HorizontalListViewHolder -> holder.bind(bestBanchans[position])
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
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
                    when (holder) {
                        is HorizontalListViewHolder -> {
                            holder.bindCartStateChangePayload(bestBanchans[position])
                        }
                    }
                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return bestBanchans[position].viewType.value
    }

    override fun getItemCount(): Int {
        return bestBanchans.size
    }

    class HorizontalListViewHolder(
        private val binding: ItemMenuHorizontalListBinding,
        private val context: Context,
        private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
    ): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
            ): HorizontalListViewHolder = HorizontalListViewHolder(
                ItemMenuHorizontalListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                parent.context,
                banchanInsertCartListener
            )
        }
        private val childAdapter: HorizontalBanchanListAdapter by lazy {
            //TODO: ViewHolder 크기 조절 -> 필요하다면 새로 생성
            HorizontalBanchanListAdapter(
                banchanInsertCartListener
            )
        }
        private var listSize: Int = 0
        private val decoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val idx = parent.getChildAdapterPosition(view)
                if (idx < 0) return
                val margin = 16.dp(context)
                val spacing = 8.dp(context)

                when (idx) {
                    0 -> {
                        outRect.left = margin
                        outRect.right = spacing
                    }
                    listSize - 1 -> {
                        outRect.right = margin
                    }
                    else -> {
                        outRect.right = spacing
                    }
                }

                outRect.bottom = 16.dp(context)
            }
        }

        init {
            binding.rvHorizontal.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                if(this.itemDecorationCount == 0)
                    addItemDecoration(decoration)
            }
        }

        fun bind(item: BestBanchanModel){
            listSize = item.banchans.size
            binding.title = item.title
            binding.adapter = childAdapter
            childAdapter.updateList(item.banchans.toList())
        }

        fun bindCartStateChangePayload(item: BestBanchanModel){
            childAdapter.updateList(item.banchans.toList())
        }

    }
}