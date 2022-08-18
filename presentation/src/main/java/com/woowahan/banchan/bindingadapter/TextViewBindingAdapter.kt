package com.woowahan.banchan.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowahan.banchan.extension.toCashString

@BindingAdapter("android:setCashString")
fun setCashString(textView: TextView, priceLong: Long) {
    val str = priceLong.toCashString() + "원"
    textView.text = str
}