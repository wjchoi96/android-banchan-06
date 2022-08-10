package com.woowahan.banchan.ui.sidedishbanchan

import android.os.Bundle
import android.view.View
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentSideDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment

class SideDishBanchanFragment: BaseFragment<FragmentSideDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_side_dish_banchan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "side dish banchan"
    }
}