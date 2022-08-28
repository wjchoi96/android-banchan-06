package com.woowahan.banchan.bindingadapter

import android.view.KeyEvent
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.woowahan.domain.model.CartModel

@BindingAdapter(value = ["android:updateItem", "android:updateQuantityMethod"])
fun updateQuantityMethod(
    editText: EditText,
    item: CartModel,
    updateQuantity: (CartModel, Boolean) -> Unit
) {
    editText.setOnFocusChangeListener { v, hasFocus ->
        val quantity = if (editText.text.isEmpty()) 0 else {
            editText.text.toString().toInt()
        }
        if (quantity > 0) {
            updateQuantity(item, !hasFocus)
        } else {
            editText.setText(item.count.toString())
        }
    }

    editText.setOnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val quantity = if (editText.text.isEmpty()) 0 else {
                editText.text.toString().toInt()
            }
            if (quantity > 0) {
                updateQuantity(item, true)
            } else {
                editText.setText(item.count.toString())
            }
            true
        }
        false
    }
}