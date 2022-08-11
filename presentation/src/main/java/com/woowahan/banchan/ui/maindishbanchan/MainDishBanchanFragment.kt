package com.woowahan.banchan.ui.maindishbanchan

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.viewmodel.MainDishBanchanViewModel
import com.woowahan.banchan.util.dp
import com.woowahan.banchan.util.showToast
import com.woowahan.domain.model.BanchanModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainDishBanchanFragment : BaseFragment<FragmentMainDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_main_dish_banchan

    private val viewModel: MainDishBanchanViewModel by viewModels()
    private val adapter: MainDishBanchanAdapter by lazy {
        MainDishBanchanAdapter(
            getString(R.string.main_dish_banchan_banner_title),
            BanchanModel.getFilterList(),
            filterSelectedListener,
            viewModel.viewModeToggleEvent
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
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            showToast(context, it)
        }

        viewModel.banchans.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        viewModel.gridViewMode.observe(viewLifecycleOwner){
            if (it) {
                setUpGridRecyclerView()
            } else {
                setUpLinearRecyclerView()
            }
            binding.rvMainDish.refresh()
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

    private fun RecyclerView.refresh() {
        val adapterRef = this.adapter
        this.adapter = null
        this.adapter = adapterRef
    }

    private val filterSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            when (position) {
                BanchanModel.FilterType.Default.value -> {
                    viewModel.filterBanchan(BanchanModel.FilterType.Default)
                }
                BanchanModel.FilterType.PriceHigher.value -> {
                    viewModel.filterBanchan(BanchanModel.FilterType.PriceHigher)
                }
                BanchanModel.FilterType.PriceLower.value -> {
                    viewModel.filterBanchan(BanchanModel.FilterType.PriceLower)
                }
                else -> {
                    viewModel.filterBanchan(BanchanModel.FilterType.SalePercentHigher)
                }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

    }
}