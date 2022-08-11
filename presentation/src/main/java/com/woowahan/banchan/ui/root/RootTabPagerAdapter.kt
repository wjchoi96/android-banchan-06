package com.woowahan.banchan.ui.root

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RootTabPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentList: List<Pair<String, Fragment>>
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].second
    }
}