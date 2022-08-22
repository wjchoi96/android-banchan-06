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
        updateQuantity(item, !hasFocus)
    }

    editText.setOnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            updateQuantity(item, true)
            true
        }
        false
    }
}