package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class WinCardOrChallengeAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = mutableListOf<Fragment>()


    fun addFragments(fragmentList: List<Fragment>){
        fragments.addAll(fragmentList)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}