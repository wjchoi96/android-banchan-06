package com.woowahan.banchan.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.ui.adapter.viewHolder.BanchanListBannerViewHolder
import com.woowahan.banchan.ui.adapter.viewHolder.CountHeaderViewHolder
import com.woowahan.banchan.ui.adapter.viewHolder.MenuVerticalViewHolder
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultBanchanAdapter(
    private val bannerTitle: String,
    private val filterTypeList: List<String>,
    private val filterSelectedListener: (Int) -> Unit,
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedFilterPosition: Int = 0
    private val banchanList = mutableListOf<BanchanModel>()
    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<BanchanModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                BanchanModelDiffUtilCallback(banchanList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList.clear()
                banchanList.addAll(newList)
                diffRes.dispatchUpdatesTo(this@DefaultBanchanAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BanchanModel.ViewType.Banner.value -> BanchanListBannerViewHolder.from(parent)
            BanchanModel.ViewType.Header.value -> CountHeaderViewHolder.from(
                parent = parent,
                filterTypeList = filterTypeList,
                filterSelectedListener = {
                    selectedFilterPosition = it
                    filterSelectedListener(it)
                },
                menuCnt = banchanList.size - 2
            )
            else -> {
                MenuVerticalViewHolder.from(parent, banchanInsertCartListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return banchanList[position].viewType.value
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BanchanListBannerViewHolder -> holder.bind(bannerTitle)
            is CountHeaderViewHolder -> holder.bind(selectedFilterPosition)
            is MenuVerticalViewHolder -> holder.bind(banchanList[position])
        }
    }

    override fun getItemCount(): Int = banchanList.size
}