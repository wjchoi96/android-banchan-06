package com.woowahan.banchan.ui.adapter.viewHolder

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ItemCountHeaderBinding

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