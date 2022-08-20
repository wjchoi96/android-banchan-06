package com.woowahan.banchan.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.DefaultCartAdapter
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.order.OrderItemActivity
import com.woowahan.banchan.ui.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : BaseActivity<ActivityCartBinding>() {
    private val viewModel: CartViewModel by viewModels()

    companion object {
        fun get(context: Context): Intent {
            return Intent(context, CartActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_cart

    private val adapter: DefaultCartAdapter by lazy {
        DefaultCartAdapter(
            selectAll = viewModel.selectAllItems,
            deleteAllSelected = viewModel.deleteAllSelectedItems,
            deleteItem = viewModel.deleteItem,
            minusClicked = viewModel.minusClicked,
            plusClicked = viewModel.plusClicked,
            orderClicked = viewModel.orderItems,
            selectItem = viewModel.selectItem
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
        binding.title = getString(R.string.cart_title)
        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener { onBackPressed() }

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
                    is CartViewModel.UiEvent.GoToOrderList -> {
                        startActivity(OrderItemActivity.get(this@CartActivity, it.orderId))
                        showToast("주문 완료!")
                    }
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