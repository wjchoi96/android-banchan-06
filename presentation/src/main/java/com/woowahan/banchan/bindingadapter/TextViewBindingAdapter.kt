package com.woowahan.banchan.bindingadapter

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowahan.banchan.extension.getTimeString
import com.woowahan.banchan.extension.toCashString
import java.util.*

@BindingAdapter("android:setCashString")
fun setCashString(textView: TextView, priceLong: Long) {
    val str = priceLong.toCashString() + "원"
    textView.text = str
}

@BindingAdapter("android:setCancelLine")
fun setCancelLine(textView: TextView, setCancelLine: Boolean) {
    when (setCancelLine) {
        true -> textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else -> textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("android:setTimeText")
fun setTimeText(textView: TextView, time: Date) {
    textView.text = time.getTimeString()
}