package com.woowahan.banchan.ui.maindishbanchan

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.ViewModeToggleBanchanAdapter
import com.woowahan.banchan.ui.adapter.decoratin.GridItemDecoration
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.extension.dp
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.util.DialogUtil
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
            viewModel.filter,
            viewModel.gridViewMode.value,
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

        setUpRecyclerView()
        observeData()
    }

    private fun setUpRecyclerView(){
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
    }

    private fun setUpGridRecyclerView() {
        (binding.rvMainDish.layoutManager as GridLayoutManager).spanCount = spanCount
        binding.rvMainDish.addItemDecoration(gridItemDecoration)
    }

    private fun setUpLinearRecyclerView() {
        binding.rvMainDish.removeItemDecoration(gridItemDecoration)
        (binding.rvMainDish.layoutManager as GridLayoutManager).spanCount = 1
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

                    is MainDishBanchanViewModel.UiEvent.ShowDialog -> {
                        DialogUtil.show(requireContext(), it.dialogBuilder)
                    }

                    is MainDishBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
                        it.bottomSheet.show(childFragmentManager, "cart_bottom_sheet")
                    }

                    is MainDishBanchanViewModel.UiEvent.ShowCartView -> {
                        startActivity(CartActivity.get(requireContext()))
                    }
                }
            }
        }

        repeatOnStarted {
            viewModel.gridViewMode.collect {
                Timber.d("gridViewMode => $it")
                when(it){
                    true -> setUpGridRecyclerView()
                    else -> setUpLinearRecyclerView()
                }
                adapter.refreshList()
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