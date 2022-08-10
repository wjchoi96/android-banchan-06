package com.woowahan.banchan.ui.maindishbanchan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment
import com.woowahan.banchan.ui.viewmodel.MainDishBanchanViewModel
import com.woowahan.banchan.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainDishBanchanFragment: BaseFragment<FragmentMainDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_main_dish_banchan

    private val viewModel: MainDishBanchanViewModel by viewModels()
    private val adapter: MainDishBanchanAdapter by lazy { MainDishBanchanAdapter(
        getString(R.string.main_dish_banchan_banner_title),
        emptyList()
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.rvMainDish.layoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return when(position<2){
                        true -> 2
                        else -> 1
                    }
                }
            }
        }
        observeData()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchMainDishBanchans()
    }

    private fun observeData(){
        viewModel.errorMessage.observe(viewLifecycleOwner){
            showToast(context, it)
        }

        viewModel.banchans.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
    }
}