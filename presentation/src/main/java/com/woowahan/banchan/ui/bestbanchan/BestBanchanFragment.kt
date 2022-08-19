package com.woowahan.banchan.ui.bestbanchan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentBestBanchanBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showSnackBar
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.adapter.BestBanchanAdapter
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.viewmodel.BestBanchanViewModel
import com.woowahan.banchan.util.DialogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestBanchanFragment: BaseFragment<FragmentBestBanchanBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_best_banchan

    private val viewModel: BestBanchanViewModel by viewModels()

    private val adapter: BestBanchanAdapter by lazy {
        BestBanchanAdapter(
            getString(R.string.best_banchan_banner_title),
            viewModel.clickInsertCartButton,
            viewModel.itemClickListener
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter

        observeData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchBestBanchans()
    }

    private fun observeData() {
        repeatOnStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is BestBanchanViewModel.UiEvent.ShowToast -> showToast(context, it.message)

                    is BestBanchanViewModel.UiEvent.ShowSnackBar -> showSnackBar(
                        it.message,
                        binding.layoutBackground
                    )

                    is BestBanchanViewModel.UiEvent.ShowDialog -> {
                        DialogUtil.show(requireContext(), it.dialogBuilder)
                    }

                    is BestBanchanViewModel.UiEvent.ShowCartBottomSheet -> {
                        it.bottomSheet.show(childFragmentManager, "cart_bottom_sheet")
                    }

                    is BestBanchanViewModel.UiEvent.ShowCartView -> {
                        startActivity(CartActivity.get(requireContext()))
                    }

                    is BestBanchanViewModel.UiEvent.ShowDetailView -> {
                        //startActivity(DetailActivity.get(requireContext()))
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