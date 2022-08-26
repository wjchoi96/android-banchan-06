package com.woowahan.banchan.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ItemCountHeaderBinding
import com.woowahan.banchan.ui.adapter.FilterSpinnerAdapter

class CountHeaderViewHolder(
    private val binding: ItemCountHeaderBinding,
    private val filterTypeList: List<String>,
    private val filterSelectedListener: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(
            parent: ViewGroup,
            filterTypeList: List<String>,
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
            )
    }

    fun bind(selectedItemPosition: Int, menuCnt: Int) {
        binding.holder = this
        binding.menuCnt = menuCnt - 2
        binding.defaultSpinnerSelectPosition = selectedItemPosition
    }

    val spinnerAdapter = FilterSpinnerAdapter(
        binding.root.context,
        R.layout.item_filter_spinner,
        R.id.tv_filter_name,
        filterTypeList
    )

    val filterSpinnerItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            filterSelectedListener(p2)
            spinnerAdapter.setSelection(p2)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }
}