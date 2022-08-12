package com.woowahan.banchan.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.woowahan.banchan.R

class FilterSpinnerAdapter(
    context: Context,
    resourceId: Int,
    textResourceId: Int,
    filterTypeList: List<String>
) : ArrayAdapter<String>(context, resourceId, textResourceId, filterTypeList) {
    private var selectedItemPosition = 0

    fun setSelection(position: Int) {
        selectedItemPosition = position
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = super.getDropDownView(position, convertView, parent)
        if (position == selectedItemPosition) {
            val textView = (view as ViewGroup).getChildAt(0) as TextView
            val imageView = view.getChildAt(1)

            textView.setTypeface(textView.typeface, Typeface.BOLD)
            imageView.visibility = View.VISIBLE
        }
        return view
    }
}