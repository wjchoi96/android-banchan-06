package com.woowahan.banchan.ui.order

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityOrderItemBinding
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.OrderItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderItemActivity : BaseActivity<ActivityOrderItemBinding>() {
    companion object {
        private const val EXTRA_ORDER_ID = "order_id"
        fun get(context: Context, orderId: Long): Intent = Intent(
            context, OrderItemActivity::class.java
        ).apply {
            putExtra(EXTRA_ORDER_ID, orderId)
        }
    }

    private val viewModel: OrderItemViewModel by viewModels()
    private val adapter: OrderItemAdapter = OrderItemAdapter()

    override val layoutResId: Int
        get() = R.layout.activity_order_item

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.dataLoading = viewModel.dataLoading.value
        binding.adapter = adapter

        viewModel.initData(intent.getLongExtra(EXTRA_ORDER_ID, -1))
        setUpToolbar()
        setUpRecyclerView()
        observeData()
    }

    private fun setUpToolbar(){
        binding.layoutIncludeToolBar.toolBar.apply {
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.menu_order_item)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_sub_action_bar_refresh -> {
                        viewModel.refreshEvent()
                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }
        }
    }

    private fun setUpRecyclerView(){
        binding.rvOrder.addItemDecoration(object: RecyclerView.ItemDecoration(){
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                c.drawColor(resources.getColor(R.color.white_ffffff, null))
            }

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val size = state.itemCount
                when(parent.getChildAdapterPosition(view)){
                    0 -> return
                    size - 1 -> return
                }
                when(parent.getChildAdapterPosition(view) - 1){
                    0 -> {
                        when(size-2){
                            1 -> outRect.bottom = 16.dp(this@OrderItemActivity)
                            else -> outRect.bottom = 8.dp(this@OrderItemActivity)
                        }
                        outRect.top = 16.dp(this@OrderItemActivity)
                    }
                    size-3 -> {
                        outRect.top = 8.dp(this@OrderItemActivity)
                        outRect.bottom = 16.dp(this@OrderItemActivity)
                    }
                    else -> {
                        outRect.top = 8.dp(this@OrderItemActivity)
                        outRect.bottom = 8.dp(this@OrderItemActivity)
                    }
                }
            }
        })
    }

    private fun observeData(){
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when(it){
                        is OrderItemViewModel.UiEvent.FinishView -> {
                            it.message?.let{ message -> showToast(message) }
                            finish()
                        }
                        is OrderItemViewModel.UiEvent.ShowToast -> showToast(it.message)
                        is OrderItemViewModel.UiEvent.ShowSnackBar -> showSnackBar(it.message, binding.layoutBackground)
                    }
                }
            }

            launch {
                viewModel.orderItem.collect {
                    adapter.updateList(it)
                }
            }

            viewModel.fetchOrder()
        }
    }
}