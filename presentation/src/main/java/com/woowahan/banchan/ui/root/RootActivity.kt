package com.woowahan.banchan.ui.root

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityMainBinding
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.bestbanchan.BestBanchanFragment
import com.woowahan.banchan.ui.maindishbanchan.MainDishBanchanFragment
import com.woowahan.banchan.ui.sidedishbanchan.SideDishBanchanFragment
import com.woowahan.banchan.ui.soupdishbanchan.SoupDishBanchanFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RootActivity: BaseActivity<ActivityMainBinding>() {

    private val fragmentList: List<Pair<String, Fragment>> by lazy {
        listOf(
            getString(R.string.best_banchan_title) to BestBanchanFragment(),
            getString(R.string.main_dish_banchan_title) to MainDishBanchanFragment(),
            getString(R.string.soup_dish_banchan_title) to SoupDishBanchanFragment(),
            getString(R.string.side_dish_banchan_title) to SideDishBanchanFragment()
        )
    }
    private val pagerAdapter: RootTabPagerAdapter by lazy {
        RootTabPagerAdapter(this, fragmentList)
    }

    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewPager()
    }

    private fun setUpViewPager(){
        binding.vpContent.adapter = pagerAdapter
        binding.vpContent.offscreenPageLimit = 1
        TabLayoutMediator(binding.layoutTab, binding.vpContent) { tab, position ->
            tab.text = fragmentList[position].first
        }.attach()
    }


}