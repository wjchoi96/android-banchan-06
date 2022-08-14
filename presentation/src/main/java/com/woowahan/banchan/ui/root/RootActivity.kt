package com.woowahan.banchan.ui.root

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityMainBinding
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.bestbanchan.BestBanchanFragment
import com.woowahan.banchan.ui.maindishbanchan.MainDishBanchanFragment
import com.woowahan.banchan.ui.sidedishbanchan.SideDishBanchanFragment
import com.woowahan.banchan.ui.soupdishbanchan.SoupDishBanchanFragment
import com.woowahan.banchan.ui.viewmodel.RootViewModel
import com.woowahan.banchan.util.repeatOnStarted
import com.woowahan.banchan.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class RootActivity: BaseActivity<ActivityMainBinding>() {

    private val viewModel: RootViewModel by viewModels()

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
        setListener()
        observeData()
    }

    private fun setListener(){
        // use at main tool bar
        binding.layoutIncludeToolBar.toolBar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when(it.itemId){
                R.id.menu_main_action_bar_cart -> {
                    showToast("cart")
                    true
                }
                R.id.menu_main_action_bar_order -> {
                    showToast("order")
                    true
                }
                else -> false
            }
        }

        // use at sub tool bar
        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener {
            showToast("back")
        }
    }

    private fun setUpViewPager(){
        binding.vpContent.adapter = pagerAdapter
        TabLayoutMediator(binding.layoutTab, binding.vpContent) { tab, position ->
            tab.text = fragmentList[position].first
        }.attach()
    }

    private fun observeData(){
        repeatOnStarted {
            viewModel.cartItemSize.collect {
                Timber.d("collect cart item size at root => $it")
            }
        }
    }


}