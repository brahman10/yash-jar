package com.jar.app.feature.transaction_new.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature_transaction.api.TransactionApi

class NewTransactionAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, transactionApi: TransactionApi) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val POSITION_GOLD_TRANSACTIONS = 0
        const val POSITION_WINNING_TRANSACTIONS = 1
    }

    private val goldTransactionFragment by lazy {
        transactionApi.openTransactionGold()
    }

    private val winningTransactionFragment by lazy {
        transactionApi.openTransactionWinning()
    }

    private val fragments = listOf(
        goldTransactionFragment,
        winningTransactionFragment
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun scrollToTop(currentPosition: Int) {
        when (currentPosition) {
            POSITION_GOLD_TRANSACTIONS -> {
                goldTransactionFragment.scrollToTop()
            }
            POSITION_WINNING_TRANSACTIONS -> {
                winningTransactionFragment.scrollToTop()
            }
        }
    }
}