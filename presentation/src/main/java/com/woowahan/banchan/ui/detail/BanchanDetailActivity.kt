package com.woowahan.banchan.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityBanchanDetailBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.ImageAdapter
import com.woowahan.banchan.ui.base.BaseNetworkActivity
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.order.OrderListActivity
import com.woowahan.banchan.ui.viewmodel.DetailViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BanchanDetailActivity : BaseNetworkActivity<ActivityBanchanDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()

    override val snackBarView: View by lazy {
        binding.layoutBackground
    }

    companion object {
        private const val HASH = "hash"
        private const val TITLE = "title"

        fun get(context: Context, hash: String, title: String): Intent {
            return Intent(context, BanchanDetailActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(HASH, hash)
                putExtra(TITLE, title)
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_banchan_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData(intent)
        setListener()
        observeData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { initData(it) }
    }

    private fun initData(intent: Intent) {
        viewModel.initDate(intent.getStringExtra(HASH), intent.getStringExtra(TITLE))
        viewModel.fetchBanchanDetail()
    }

    private fun setListener() {
        binding.layoutIncludeToolBar.toolBar.setOnMenuItemClickListener {
            Timber.d("menu item click => ${it.title}")
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.menu_main_action_bar_order -> {
                    startActivity(OrderListActivity.get(this))
                    true
                }
                else -> false
            }
        }

        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_cart).actionView.let {
            (it.findViewById(R.id.iv_cart_image) as ImageView).setOnClickListener {
                startActivity(CartActivity.get(this))
            }
        }
    }

    private fun observeData() {
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when (it) {
                        is DetailViewModel.UiEvent.ShowToast -> showToast(it.message)
                        is DetailViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                            it.message,
                            binding.layoutBackground
                        )
                        is DetailViewModel.UiEvent.ShowDialog -> {
                            DialogUtil.show(this@BanchanDetailActivity, it.dialogBuilder)
                        }
                        is DetailViewModel.UiEvent.ShowCartView -> {
                            startActivity(CartActivity.get(this@BanchanDetailActivity))
                        }
                        is DetailViewModel.UiEvent.FinishView -> {
                            it.message?.let {
                                DialogUtil.show(this@BanchanDetailActivity, it, "닫기") {
                                    finish()
                                }
                            }
                        }
                    }
                }
            }

            launch {
                viewModel.detail.collect {
                    binding.banchanDetail = it
                    binding.vm = viewModel
                    binding.imageAdapter =
                        ImageAdapter(it.detailImages, ImageAdapter.ImageType.DETAIL)
                    binding.isCartSelected = it.isCartItem

                    if (it.isNotEmpty()) {
                        viewModel.insertRecentViewedItem(it.hash, it.title)
                        binding.vpMenuImages.adapter =
                            ImageAdapter(it.thumbImages, ImageAdapter.ImageType.THUMB)
                        TabLayoutMediator(
                            binding.layoutTabIndicator,
                            binding.vpMenuImages
                        ) { tab, position ->
                        }.attach()
                    }
                }
            }

            repeatOnStarted {
                launch {
                    viewModel.cartItemSize.collect {
                        setupBadge(it)
                    }
                }

                launch {
                    viewModel.deliveryItemSize.collect {
                        setOrderBadge(it != 0)
                    }
                }
            }
        }
    }

    private fun setupBadge(count: Int) {
        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_cart).actionView.let {
            (it.findViewById(R.id.cart_badge) as TextView).let { tv ->
                tv.isVisible = count != 0
                tv.text = if (count <= 10) count.toString() else "10+"
            }
        }
    }

    private fun setOrderBadge(on: Boolean) {
        val iconRes = when (on) {
            true -> R.drawable.ic_mypage_badge
            else -> R.drawable.ic_mypage
        }
        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_order)
            .setIcon(iconRes)
    }
}