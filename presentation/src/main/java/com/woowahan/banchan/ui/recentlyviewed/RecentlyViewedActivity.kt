package com.woowahan.banchan.ui.recentlyviewed

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityRecentlyViewedBinding
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.RecentlyViewedAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.RecentlyViewedViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RecentlyViewedActivity : BaseActivity<ActivityRecentlyViewedBinding>() {

    companion object {
        fun get(context: Context): Intent {
            return Intent(context, RecentlyViewedActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_recently_viewed

    private val viewModel: RecentlyViewedViewModel by viewModels()
    private val spanCount = 2
    private val adapter: RecentlyViewedAdapter by lazy {
        RecentlyViewedAdapter(
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
        (binding.rvRecentlyViewed.layoutManager as GridLayoutManager).spanCount = spanCount
        binding.rvRecentlyViewed.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                val margin = 16.dp(this@RecentlyViewedActivity)
                val spacing = 8.dp(this@RecentlyViewedActivity)
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

                outRect.top = 16.dp(this@RecentlyViewedActivity)
                outRect.bottom = 16.dp(this@RecentlyViewedActivity)
                Timber.d("idx[$idx] => left[${outRect.left}], right[${outRect.right}]")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchMainDishBanchans()
    }

    private fun observeData() {
        repeatOnStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is RecentlyViewedViewModel.UiEvent.ShowToast -> showToast(it.message)

                    is RecentlyViewedViewModel.UiEvent.ShowSnackBar -> showSnackBar(it.message, binding.layoutBackground)

                    is RecentlyViewedViewModel.UiEvent.ShowDialog -> {
                        DialogUtil.show(this@RecentlyViewedActivity, it.dialogBuilder)
                    }

                    is RecentlyViewedViewModel.UiEvent.ShowCartBottomSheet -> {
                        it.bottomSheet.show(supportFragmentManager, "cart_bottom_sheet")
                    }

                    is RecentlyViewedViewModel.UiEvent.ShowCartView -> {
                        //TODO: startActivity(CartActivity.get(requireContext())
                    }

                    is RecentlyViewedViewModel.UiEvent.ShowDetailView -> {
                        //TODO: startActivity(DetailActivity.get(requireContext())
                    }
                }
            }
        }

        repeatOnStarted {
            viewModel.banchans.collect {
                adapter.updateList(it)
            }
        }
    }

}