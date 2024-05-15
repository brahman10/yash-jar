package com.jar.app.feature_transaction.api

import com.jar.app.feature_transaction.impl.ui.gold.NewGoldFragment
import com.jar.app.feature_transaction.impl.ui.winning.NewWinningFragment

interface TransactionApi {

    fun openTransactionGold(): NewGoldFragment

    fun openTransactionWinning(): NewWinningFragment

    fun openTransactionDetailBottomSheet(id:String)
}