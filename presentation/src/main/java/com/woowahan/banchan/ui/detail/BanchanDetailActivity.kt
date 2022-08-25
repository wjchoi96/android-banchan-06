package com.woowahan.banchan.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityBanchanDetailBinding
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.ImageAdapter
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.viewmodel.BestBanchanViewModel
import com.woowahan.banchan.ui.viewmodel.DetailViewModel
import com.woowahan.banchan.ui.viewmodel.OrderItemViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BanchanDetailActivity : BaseActivity<ActivityBanchanDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()

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

        viewModel.initDate(intent.getStringExtra(HASH), intent.getStringExtra(TITLE))
        viewModel.fetchBanchanDetail()
        observeData()
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
                    }
                }
            }

            launch {
                viewModel.detail.collect {
                    binding.banchanDetail = it
                    binding.vm = viewModel
                    binding.imageAdapter =
                        ImageAdapter(it.detailImages, ImageAdapter.ImageType.DETAIL)

                    if (it.isNotEmpty()) {
                        viewModel.insertRecentViewedItem(it.toBanchanModel())
                        binding.vpMenuImages.adapter = ImageAdapter(it.thumbImages, ImageAdapter.ImageType.THUMB)
                        TabLayoutMediator(binding.layoutTabIndicator, binding.vpMenuImages) { tab, position ->

                        }.attach()
                    }
                }
            }
        }
    }
}