package com.woowahan.banchan.ui.cart

import androidx.activity.viewModels
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.CartViewModel

class CartActivity : BaseActivity<ActivityCartBinding>() {
    private val viewModel: CartViewModel by viewModels()

    override val layoutResId: Int
        get() = R.layout.activity_cart
}