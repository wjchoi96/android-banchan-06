package com.woowahan.banchan.ui.sidedishbanchan

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentSideDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.viewmodel.SideDishBanchanViewModel
import com.woowahan.banchan.util.dp
import com.woowahan.banchan.util.repeatOnStarted
import com.woowahan.banchan.util.showSnackBar
import com.woowahan.banchan.util.showToast
import com.woowahan.domain.model.BanchanModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SideDishBanchanFragment: BaseFragment<FragmentSideDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_side_dish_banchan


    private val viewModel: SideDishBanchanViewModel by viewModels()
    private val adapter: SideDishBanchanAdapter by lazy {
        SideDishBanchanAdapter(
            getString(R.string.side_dish_banchan_title),
            BanchanModel.getFilterList(),
            viewModel.filterItemSelect
        )
    }

    private val spanCount = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter

        setUpGridRecyclerView()
        observeData()
    }

    private fun setUpGridRecyclerView() {
        binding.rvSideDish.layoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position < 2) {
                        true -> spanCount
                        else -> 1
                    }
                }
            }
        }

        binding.rvSideDish.addItemDecoration(gridItemDecoration)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchSoupDishBanchans()
    }

    private fun observeData() {
        repeatOnStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is SideDishBanchanViewModel.UiEvent.ShowToast -> showToast(context, it.message)
                    is SideDishBanchanViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                        it.message,
                        binding.layoutBackground
                    )
                }
            }
        }

        repeatOnStarted {
            viewModel.banchans.collect {
                adapter.updateList(it)
            }
        }
    }

    private val gridItemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val idx = parent.getChildAdapterPosition(view) - 2
            if (idx < 0) return
            val column = idx % spanCount
            val margin = 16.dp(context)
            val spacing = 8.dp(context)
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

            outRect.bottom = 32.dp(context)
            Timber.d("idx[$idx] => left[${outRect.left}], right[${outRect.right}]")
        }
    }
}