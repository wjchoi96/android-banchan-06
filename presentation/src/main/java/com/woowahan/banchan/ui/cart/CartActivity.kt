package com.woowahan.banchan.ui.cart

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.woowahan.banchan.R
import com.woowahan.banchan.background.DeliveryAlarmReceiver
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.DefaultCartAdapter
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.detail.BanchanDetailActivity
import com.woowahan.banchan.ui.order.OrderItemActivity
import com.woowahan.banchan.ui.order.OrderListActivity
import com.woowahan.banchan.ui.recentviewed.RecentViewedActivity
import com.woowahan.banchan.ui.viewmodel.CartViewModel
import com.woowahan.banchan.ui.viewmodel.MainDishBanchanViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

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

    private val cartAdapter: DefaultCartAdapter by lazy {
        DefaultCartAdapter(
            selectAll = viewModel.selectAllItems,
            deleteAllSelected = viewModel.deleteAllSelectedItems,
            deleteItem = viewModel.deleteItem,
            updateItem = viewModel.updateItemCount,
            orderClicked = viewModel.orderItems,
            selectItem = viewModel.selectItem,
            recentViewedAllClicked = {
                startActivity(RecentViewedActivity.get(this))
            },
            itemClickListener = viewModel.itemClickListener
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchCartItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.adapter = cartAdapter
        binding.title = getString(R.string.cart_title)
        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener { onBackPressed() }

        observeData()
    }

    private fun observeData() {
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when (it) {
                        is CartViewModel.UiEvent.ShowToast -> showToast(it.message)
                        is CartViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                            it.message,
                            binding.layoutBackground
                        )
                        is CartViewModel.UiEvent.GoToOrderList -> {
                            orderNavigateLauncher.launch(
                                OrderItemActivity.get(
                                    this@CartActivity,
                                    it.orderId
                                )
                            )
                        }
                        is CartViewModel.UiEvent.DeliveryAlarmSetting -> {
                            setDeliveryAlarm(
                                it.orderId,
                                it.orderTitle,
                                it.orderItemCount,
                                it.minute
                            )
                        }
                    }
                }
            }

            repeatOnStarted {
                viewModel.cartItems.collect {
                    cartAdapter.updateList(it)
                }
            }

            repeatOnStarted {
                viewModel.eventFlow.collect {
                    when (it) {
                        is CartViewModel.UiEvent.ShowDetailView -> {
                            startActivity(BanchanDetailActivity.get(this@CartActivity, it.banchanModel.hash, it.banchanModel.title))
                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = currentFocus
        view?.let {
            val rect = Rect()
            val x = ev?.x
            val y = ev?.y

            if (x != null && y != null) {
                view.getGlobalVisibleRect(rect)
                if (!rect.contains(x.toInt(), y.toInt())) {
                    val imm = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    imm.let {
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)

    }

    private fun setDeliveryAlarm(
        orderId: Long,
        orderTitle: String?,
        orderItemCount: Int,
        minute: Int
    ) {
        val title = getString(R.string.order_items_title, orderTitle ?: "상품", orderItemCount)
        val alarmManager = getSystemService(AlarmManager::class.java)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().time.time + (minute * 60) * 1000,
            PendingIntent.getBroadcast(
                this,
                orderId.toInt(),
                DeliveryAlarmReceiver.get(this, orderId, title),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    private val orderNavigateLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                when (viewModel.isCartItemIsEmpty) {
                    true -> {
                        startActivity(OrderListActivity.get(this))
                        finish()
                    }
                    else -> {}
                }
            }
        }
}
