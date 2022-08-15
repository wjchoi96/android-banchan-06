package com.woowahan.banchan.ui.root

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityMainBinding
import com.woowahan.banchan.extension.repeatOnStarted
import com.woowahan.banchan.extension.showToast
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.bestbanchan.BestBanchanFragment
import com.woowahan.banchan.ui.cart.CartActivity
import com.woowahan.banchan.ui.maindishbanchan.MainDishBanchanFragment
import com.woowahan.banchan.ui.sidedishbanchan.SideDishBanchanFragment
import com.woowahan.banchan.ui.soupdishbanchan.SoupDishBanchanFragment
import com.woowahan.banchan.ui.viewmodel.RootViewModel
import dagger.hilt.android.AndroidEntryPoint
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
            Timber.d("menu item click => ${it.title}")
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

        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_cart).actionView.let {
            (it.findViewById(R.id.iv_cart_image) as ImageView).setOnClickListener {
                val cartIntent = Intent(this, CartActivity::class.java)
                cartIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(cartIntent)
            }
        }

        // use at sub tool bar
        binding.layoutIncludeToolBar.toolBar.setNavigationOnClickListener {
            showToast("back")
        }
    }

    private fun setUpViewPager(){
        binding.vpContent.adapter = pagerAdapter
        binding.vpContent.offscreenPageLimit = 1
        TabLayoutMediator(binding.layoutTab, binding.vpContent) { tab, position ->
            tab.text = fragmentList[position].first
        }.attach()
    }

    private fun observeData(){
        repeatOnStarted {
            viewModel.cartItemSize.collect {
                setupBadge(it)
            }
        }
    }

    private fun setupBadge(count: Int) {
        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_cart).actionView.let {
            (it.findViewById(R.id.cart_badge) as TextView).let { tv ->
                tv.isVisible = count != 0
                tv.text = if(count <= 10) count.toString() else "10+"
            }
        }
    }

    private fun setOrderBadge(on: Boolean){
        val iconRes = when(on){
            true -> R.drawable.ic_mypage_badge
            else -> R.drawable.ic_mypage
        }
        binding.layoutIncludeToolBar.toolBar.menu.findItem(R.id.menu_main_action_bar_order).setIcon(iconRes)
    }


}