package com.woowahan.banchan.ui.maindishbanchan

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.ui.adapter.ViewModeToggleBanchanAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.viewmodel.MainDishBanchanViewModel
import com.woowahan.banchan.util.dp
import com.woowahan.banchan.util.repeatOnStarted
import com.woowahan.banchan.util.showSnackBar
import com.woowahan.banchan.util.showToast
import com.woowahan.domain.model.BanchanModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainDishBanchanFragment : BaseFragment<FragmentMainDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_main_dish_banchan

    private val viewModel: MainDishBanchanViewModel by viewModels()
    private val adapter: ViewModeToggleBanchanAdapter by lazy {
        ViewModeToggleBanchanAdapter(
            getString(R.string.main_dish_banchan_banner_title),
            BanchanModel.getFilterList(),
            viewModel.filterItemSelect,
            viewModel.viewModeToggleEvent,
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
        binding.rvMainDish.layoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position < 2) {
                        true -> spanCount
                        else -> 1
                    }
                }
            }
        }

        binding.rvMainDish.addItemDecoration(gridItemDecoration)
    }

    private fun setUpLinearRecyclerView() {
        binding.rvMainDish.removeItemDecoration(gridItemDecoration)
        binding.rvMainDish.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchMainDishBanchans()
    }

    private fun observeData() {
        repeatOnStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is MainDishBanchanViewModel.UiEvent.ShowToast -> showToast(context, it.message)
                    is MainDishBanchanViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                        it.message,
                        binding.layoutBackground
                    )
                    is MainDishBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
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

        repeatOnStarted {
            viewModel.gridViewMode.collect() {
                if (it) {
                    setUpGridRecyclerView()
                } else {
                    setUpLinearRecyclerView()
                }
                binding.rvMainDish.refresh()
            }
        }

    }

    private val gridItemDecoration by lazy {
        GridItemDecoration(
            requireContext(),
            spanCount
        ).decoration
    }

    private fun RecyclerView.refresh() {
        val adapterRef = this.adapter
        this.adapter = null
        this.adapter = adapterRef
    }

}