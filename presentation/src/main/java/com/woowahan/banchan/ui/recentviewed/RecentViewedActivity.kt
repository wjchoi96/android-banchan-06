package com.woowahan.banchan.ui.recentviewed

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityRecentViewedBinding
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.RecentViewedAdapter
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.viewmodel.RecentViewedViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RecentViewedActivity : BaseActivity<ActivityRecentViewedBinding>() {

    companion object {
        fun get(context: Context): Intent {
            return Intent(context, RecentViewedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_recent_viewed

    private val viewModel: RecentViewedViewModel by viewModels()
    private val spanCount = 2
    private val adapter: RecentViewedAdapter by lazy {
        RecentViewedAdapter(
            viewModel.clickInsertCartButton,
            viewModel.itemClickListener
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.title = getString(R.string.recent_viewed_title)

        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener { onBackPressed() }
        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView() {
        (binding.rvRecentViewed.layoutManager as GridLayoutManager).spanCount = spanCount
        binding.rvRecentViewed.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val idx = parent.getChildAdapterPosition(view)
                if (idx < 0) return
                val column = idx % spanCount
                val margin = 16.dp(this@RecentViewedActivity)
                val spacing = 8.dp(this@RecentViewedActivity)
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                when (column) {
                    0 -> {
                        outRect.left = margin
                    }
                    spanCount - 1 -> {
                        outRect.right = margin
                    }
                }

                outRect.top = 16.dp(this@RecentViewedActivity)
                outRect.bottom = 16.dp(this@RecentViewedActivity)
                Timber.d("idx[$idx] => left[${outRect.left}], right[${outRect.right}]")
            }
        })
    }

    private fun observeData() {
        repeatOnStarted {
            launch {
                viewModel.eventFlow.collect {
                    when (it) {
                        is RecentViewedViewModel.UiEvent.ShowToast -> showToast(it.message)

                        is RecentViewedViewModel.UiEvent.ShowSnackBar -> showSnackBar(it.message, binding.layoutBackground)

                        is RecentViewedViewModel.UiEvent.ShowDialog -> {
                            DialogUtil.show(this@RecentViewedActivity, it.dialogBuilder)
                        }

                        is RecentViewedViewModel.UiEvent.ShowCartBottomSheet -> {
                            it.bottomSheet.show(supportFragmentManager, "cart_bottom_sheet")
                        }

                        is RecentViewedViewModel.UiEvent.ShowCartView -> {
                            startActivity(CartActivity.get(this@RecentViewedActivity))
                            finish()
                        }

                        is RecentViewedViewModel.UiEvent.ShowDetailView -> {
                            //TODO: startActivity(DetailActivity.get(requireContext())
                        }
                    }
                }
            }

            launch {
                viewModel.banchans.collectLatest {
                    adapter.updateList(it)
                }
            }
        }

    }

}