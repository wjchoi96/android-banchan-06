package com.woowahan.banchan.ui.cart

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.DefaultCartAdapter
import com.woowahan.banchan.ui.adapter.ViewModeToggleBanchanAdapter
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.CartViewModel
import com.woowahan.banchan.ui.viewmodel.SideDishBanchanViewModel
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.model.CartModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding>() {
    private val viewModel: CartViewModel by viewModels()

    override val layoutResId: Int
        get() = R.layout.activity_cart

    private val adapter: DefaultCartAdapter by lazy {
        DefaultCartAdapter(
            selectAll = {},
            deleteAllSelected = {},
            selectItem = { cartModel, isChecked ->

            },
            deleteItem = {},
            minusClicked = {},
            plusClicked = {},
            orderClicked = {}
        )
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchCartItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.adapter = adapter

        observeData()
    }

    private fun observeData() {
        repeatOnStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is CartViewModel.UiEvent.ShowToast -> showToast(it.message)
                    is CartViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                        it.message,
                        binding.layoutBackground
                    )
                }
            }
        }

        repeatOnStarted {
            viewModel.cartItems.collect {
                adapter.updateList(it)
            }
        }
    }
}