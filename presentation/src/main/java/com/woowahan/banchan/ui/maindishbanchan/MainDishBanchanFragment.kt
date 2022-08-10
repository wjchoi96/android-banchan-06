package com.woowahan.banchan.ui.maindishbanchan

import android.os.Bundle
import android.view.View
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentMainDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment

class MainDishBanchanFragment: BaseFragment<FragmentMainDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_main_dish_banchan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "main dish banchan"
    }
}