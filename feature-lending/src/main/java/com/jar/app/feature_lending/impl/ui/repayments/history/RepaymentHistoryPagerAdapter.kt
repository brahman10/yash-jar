package com.jar.app.feature_lending.impl.ui.repayments.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

internal class RepaymentHistoryPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val loanId:String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val emiFragment by lazy {
        RepaymentEmiScheduleFragment.newInstance(loanId)
    }

    private val transactionFragment by lazy {
        RepaymentTxnHistoryFragment.newInstance(loanId)
    }

    private val fragments = listOf(emiFragment, transactionFragment)

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}