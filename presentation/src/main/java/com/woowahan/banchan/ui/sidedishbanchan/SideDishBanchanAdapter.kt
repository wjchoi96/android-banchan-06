package com.woowahan.banchan.ui.sidedishbanchan

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.*
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SideDishBanchanAdapter(
    private val bannerTitle: String,
    private val filterTypeList: List<String>,
    private val filterSelectedListener: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedFilterPosition: Int = 0
    private val banchanList = mutableListOf<BanchanModel>()

    fun updateList(newList: List<BanchanModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback = BanchanModelDiffUtilCallback(banchanList, newList)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList.clear()
                banchanList.addAll(newList)
                diffRes.dispatchUpdatesTo(this@SideDishBanchanAdapter)
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
                MainDishBanchanVerticalViewHolder.from(parent)
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
            is MainDishBanchanVerticalViewHolder -> holder.bind(banchanList[position])
        }
    }

    override fun getItemCount(): Int = banchanList.size

    class BanchanListBannerViewHolder(
        private val binding: ItemBachanListBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): BanchanListBannerViewHolder = BanchanListBannerViewHolder(
                ItemBachanListBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        fun bind(title: String) {
            binding.bannerTitle = title
            binding.showBestLabel = false
        }
    }

    class CountHeaderViewHolder(
        private val binding: ItemCountHeaderBinding,
        private val filterTypeList: List<String>,
        private val menuCnt: Int,
        private val filterSelectedListener: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                filterTypeList: List<String>,
                menuCnt: Int,
                filterSelectedListener: (Int) -> Unit
            ): CountHeaderViewHolder =
                CountHeaderViewHolder(
                    binding = ItemCountHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    filterTypeList = filterTypeList,
                    filterSelectedListener = filterSelectedListener,
                    menuCnt = menuCnt
                )
        }

        fun bind(selectedItemPosition: Int) {
            binding.holder = this
            binding.menuCnt = menuCnt
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

        val filterSpinnerItemSelectListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filterSelectedListener(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    class MainDishBanchanVerticalViewHolder(
        private val binding: ItemMenuVerticalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): MainDishBanchanVerticalViewHolder =
                MainDishBanchanVerticalViewHolder(
                    ItemMenuVerticalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bind(item: BanchanModel) {
            binding.banchan = item
        }
    }
}