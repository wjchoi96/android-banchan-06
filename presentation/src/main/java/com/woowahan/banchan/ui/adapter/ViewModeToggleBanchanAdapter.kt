package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ItemViewModeToggleHeaderBinding
import com.woowahan.banchan.ui.adapter.viewHolder.BanchanListBannerViewHolder
import com.woowahan.banchan.ui.adapter.viewHolder.MenuHorizontalViewHolder
import com.woowahan.banchan.ui.adapter.viewHolder.MenuVerticalViewHolder
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ViewModeToggleBanchanAdapter(
    private val bannerTitle: String,
    defaultFilter: BanchanModel.FilterType,
    defaultViewMode: Boolean,
    private val filterTypeList: List<String>,
    private val filterSelectedListener: (Int) -> Unit,
    private val viewTypeListener: (Boolean) -> Unit,
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit),
    private val itemClickListener: (BanchanModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isGridView: Boolean = defaultViewMode
    private var selectedItemPosition: Int = defaultFilter.value
    private var banchanList = listOf<BanchanModel>()

    private val cartStateChangePayload: String = "changePayload"

    suspend fun updateList(newList: List<BanchanModel>) {
        withContext(Dispatchers.Default) {
            val diffCallback =
                BanchanModelDiffUtilCallback(banchanList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList = newList.toList()
                diffRes.dispatchUpdatesTo(this@ViewModeToggleBanchanAdapter)
            }
        }
    }

    fun refreshList() {
        if (banchanList.size < 3) return
        notifyItemRangeChanged(2, banchanList.size - 2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.d("onCreateViewHolder")
        return when (viewType) {
            BanchanModel.ViewType.Banner.value -> BanchanListBannerViewHolder.from(parent)
            BanchanModel.ViewType.Header.value -> ViewModelToggleHeaderViewHolder.from(
                parent = parent,
                filterTypeList = filterTypeList,
                filterSelectedListener = {
                    selectedItemPosition = it
                    filterSelectedListener(it)
                },
                viewTypeListener = {
                    isGridView = it
                    viewTypeListener(it)
                })
            else -> {
                when (isGridView) {
                    true -> MenuVerticalViewHolder.from(
                        parent,
                        banchanInsertCartListener,
                        itemClickListener
                    )
                    false -> MenuHorizontalViewHolder.from(
                        parent,
                        banchanInsertCartListener,
                        itemClickListener
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            banchanList[position].viewType.value != BanchanModel.ViewType.Item.value ->
                banchanList[position].viewType.value
            // isGridView 가 변경되었을때, onCreateViewHolder 가 호출될 수 있도록 viewType 을 다르게 해준다
            else -> when (isGridView) {
                true -> banchanList[position].viewType.value
                else -> banchanList[position].viewType.value + 1
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.d("onBindViewHolder[$position]")
        when (holder) {
            is BanchanListBannerViewHolder -> holder.bind(bannerTitle)
            is ViewModelToggleHeaderViewHolder -> holder.bind(selectedItemPosition, isGridView)
            is MenuVerticalViewHolder -> holder.bind(banchanList[position])
            is MenuHorizontalViewHolder -> holder.bind(banchanList[position])
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
                        is MenuVerticalViewHolder -> {
                            holder.bindCartStateChangePayload(banchanList[position])
                        }
                        is MenuHorizontalViewHolder -> {
                            holder.bindCartStateChangePayload(banchanList[position])
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = banchanList.size

    class ViewModelToggleHeaderViewHolder(
        private val binding: ItemViewModeToggleHeaderBinding,
        private val filterTypeList: List<String>,
        private val filterSelectedListener: (Int) -> Unit,
        private val viewTypeListener: (Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                filterTypeList: List<String>,
                filterSelectedListener: (Int) -> Unit,
                viewTypeListener: (Boolean) -> Unit,
            ): ViewModelToggleHeaderViewHolder =
                ViewModelToggleHeaderViewHolder(
                    binding = ItemViewModeToggleHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    filterTypeList = filterTypeList,
                    filterSelectedListener = filterSelectedListener,
                    viewTypeListener = viewTypeListener
                )
        }

        fun bind(selectedItemPosition: Int, isGridView: Boolean) {
            binding.holder = this
            binding.isGridView = isGridView
            binding.defaultSpinnerSelectPosition = selectedItemPosition
        }

        val spinnerAdapter = FilterSpinnerAdapter(
            binding.root.context,
            R.layout.item_filter_spinner,
            R.id.tv_filter_name,
            filterTypeList
        )

        val rgViewGroupCheckChangedListener = RadioGroup.OnCheckedChangeListener { p0, p1 ->
            viewTypeListener(p1 == R.id.rb_grid)
        }

        val filterSpinnerItemSelectListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerAdapter.setSelection(p2)
                filterSelectedListener(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}