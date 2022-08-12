package com.woowahan.banchan.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.TextView
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
    private val filterTypeList: List<String>,
    private val filterSelectedListener: (Int) -> Unit,
    private val viewTypeListener: (Boolean) -> Unit,
    private val banchanInsertCartListener: (BanchanModel, Boolean) -> (Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isGridView: Boolean = true
    private var selectedItemPosition: Int = 0
    private var banchanList = listOf<BanchanModel>()

    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<BanchanModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback = BanchanModelDiffUtilCallback(banchanList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList = newList.toList()
                diffRes.dispatchUpdatesTo(this@ViewModeToggleBanchanAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BanchanModel.ViewType.Banner.value -> BanchanListBannerViewHolder.from(parent)
            BanchanModel.ViewType.Header.value -> ViewModelToggleHeaderViewHolder.from(
                parent = parent,
                isGridView = isGridView,
                filterTypeList = filterTypeList,
                filterSelectedListener = {
                    selectedItemPosition = it
                    filterSelectedListener(it)
                },
                viewTypeListener = {
                    viewTypeListener(it)
                    isGridView = it
                })
            else -> {
                when(isGridView){
                    true -> MenuVerticalViewHolder.from(
                        parent,
                        banchanInsertCartListener
                    )
                    false -> MenuHorizontalViewHolder.from(
                        parent,
                        banchanInsertCartListener
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return banchanList[position].viewType.value
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.d("onBindViewHolder[$position]")
        when (holder) {
            is BanchanListBannerViewHolder -> holder.bind(bannerTitle)
            is ViewModelToggleHeaderViewHolder -> holder.bind(selectedItemPosition)
            is MenuVerticalViewHolder -> holder.bind(banchanList[position])
            is MenuHorizontalViewHolder -> holder.bind(banchanList[position])
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        payloads.firstOrNull()?.let {
            Timber.d("onBindViewHolder payloads[$position][$it]")
            when(it){
                cartStateChangePayload -> {
                    when(holder){
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
        private val isGridView: Boolean,
        private val filterTypeList: List<String>,
        private val filterSelectedListener: (Int) -> Unit,
        private val viewTypeListener: (Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                isGridView: Boolean,
                filterTypeList: List<String>,
                filterSelectedListener: (Int) -> Unit,
                viewTypeListener: (Boolean) -> Unit,
            ): ViewModelToggleHeaderViewHolder =
                ViewModelToggleHeaderViewHolder(
                    binding= ItemViewModeToggleHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    isGridView= isGridView,
                    filterTypeList= filterTypeList,
                    filterSelectedListener= filterSelectedListener,
                    viewTypeListener= viewTypeListener
                )
        }

        fun bind(selectedItemPosition: Int) {
            binding.holder = this
            binding.isGridView = isGridView
            binding.defaultSpinnerSelectPosition = selectedItemPosition
        }

        val spinnerAdapter: ArrayAdapter<String> by lazy {
            object : ArrayAdapter<String>(
                binding.root.context,
                R.layout.item_filter_spinner,
                R.id.tv_filter_name,
                filterTypeList
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    if (position == binding.spinnerFilterType.selectedItemPosition) {
                        val textView = (view as ViewGroup).getChildAt(0) as TextView
                        val imageView = view.getChildAt(1)

                        textView.setTypeface(textView.typeface, Typeface.BOLD)
                        imageView.visibility = View.VISIBLE
                    }
                    return view
                }
            }
        }

        val rgViewGroupCheckChangedListener = RadioGroup.OnCheckedChangeListener { p0, p1 ->
                viewTypeListener(p1 == R.id.rb_grid)
            }

        val filterSpinnerItemSelectListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterSelectedListener(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}