package com.woowahan.banchan.ui.order

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityOrderListBinding
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.OrderListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderListActivity : BaseActivity<ActivityOrderListBinding>() {
    companion object {
        fun get(context: Context): Intent = Intent(context, OrderListActivity::class.java)
    }

    override val layoutResId: Int
        get() = R.layout.activity_order_list

    private val viewModel: OrderListViewModel by viewModels()
    private val adapter: OrderListPagingAdapter by lazy {
        OrderListPagingAdapter(viewModel.orderDetailNavigateEvent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.adapter = adapter

        setUpToolbar()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpToolbar(){
        binding.title = getString(R.string.order_title)
        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setUpRecyclerView(){
        binding.rvOrder.addItemDecoration(object: RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = 9.dp(this@OrderListActivity)
                if(parent.getChildAdapterPosition(view) == state.itemCount-1){
                    outRect.bottom = 9.dp(this@OrderListActivity)
                }
            }
        })
    }

    private fun observeData(){
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when(it){
                        is OrderListViewModel.UiEvent.ShowToast -> showToast(it.message)
                        is OrderListViewModel.UiEvent.ShowSnackBar -> showSnackBar(it.message, binding.layoutBackground)
                        is OrderListViewModel.UiEvent.NavigateOrderItemView ->
                            startActivity(OrderItemActivity.get(this@OrderListActivity, it.orderId))
                    }
                }
            }

            launch {
                viewModel.orderPaging.collect {
                    adapter.submitData(it)
                }
            }
        }
    }
}