package com.jar.app.feature.home.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature.account.AccountFragment
import com.jar.app.feature.transaction_new.ui.NewTransactionFragment
import com.jar.app.feature_homepage.api.data.HomePageApi

class HomePagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    homePageApi: HomePageApi
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val POSITION_HOME = 0
        const val POSITION_TRANSACTION = 1
        const val POSITION_ACCOUNT = 2
    }

    private val homeFragment by lazy {
        homePageApi.openHomeFragment()
    }

    private val transactionFragment by lazy {
        NewTransactionFragment.newInstance()
    }

    private val accountFragment by lazy {
        AccountFragment.newInstance()
    }

    private val fragments = listOf(homeFragment, transactionFragment, accountFragment)

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}