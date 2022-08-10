package com.woowahan.banchan.ui.soupdishbanchan

import android.os.Bundle
import android.view.View
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentSoupDishBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment

class SoupDishBanchanFragment: BaseFragment<FragmentSoupDishBanchanBinding>() {

    override val layoutResId: Int
        get() = R.layout.fragment_soup_dish_banchan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "soup dish banchan"
    }
}