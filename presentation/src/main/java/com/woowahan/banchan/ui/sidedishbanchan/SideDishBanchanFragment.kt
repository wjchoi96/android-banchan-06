package com.woowahan.banchan.ui.sidedishbanchan

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentSideDishBanchanBinding
import com.woowahan.banchan.ui.adapter.DefaultBanchanAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
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
class SideDishBanchanFragment : BaseFragment<FragmentSideDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_side_dish_banchan


    private val viewModel: SideDishBanchanViewModel by viewModels()
    private val adapter: DefaultBanchanAdapter by lazy {
        DefaultBanchanAdapter(
            getString(R.string.side_dish_banchan_title),
            BanchanModel.getFilterList(),
            viewModel.filterItemSelect,
            viewModel.clickInsertCartButton
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
                    is SideDishBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
                        it.bottomSheet.show(childFragmentManager, "cart_bottom_sheet")
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

    private val gridItemDecoration by lazy {
        GridItemDecoration(
            requireContext(),
            spanCount
        ).decoration
    }
}