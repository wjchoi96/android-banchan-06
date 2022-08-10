package com.woowahan.banchan.ui.bestbanchan

import android.os.Bundle
import android.view.View
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.FragmentBestBanchanBinding
import com.woowahan.banchan.ui.base.BaseFragment

class BestBanchanFragment: BaseFragment<FragmentBestBanchanBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_best_banchan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = "best banchan"
    }
}