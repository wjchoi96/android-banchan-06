package com.woowahan.banchan.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.ui.adapter.viewHolder.MenuVerticalViewHolder
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HorizontalBanchanListAdapter(
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
): RecyclerView.Adapter<MenuVerticalViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuVerticalViewHolder {
        return MenuVerticalViewHolder.from(
            parent,
            banchanInsertCartListener
        )
    }

    override fun onBindViewHolder(holder: MenuVerticalViewHolder, position: Int) {
        holder.bind(banchanList[position])
    }

    override fun getItemCount(): Int {
        return banchanList.size
    }
}