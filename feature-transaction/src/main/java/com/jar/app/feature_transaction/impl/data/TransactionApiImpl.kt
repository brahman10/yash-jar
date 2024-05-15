package com.jar.app.feature_transaction.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_transaction.api.TransactionApi
import com.jar.app.feature_transaction.impl.ui.gold.NewGoldFragment
import com.jar.app.feature_transaction.impl.ui.winning.NewWinningFragment
import dagger.Lazy
import javax.inject.Inject

internal class TransactionApiImpl @Inject constructor(
    navControllerRef: Lazy<NavController>
) : TransactionApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openTransactionGold(): NewGoldFragment {
        return NewGoldFragment.newInstance()
    }

    override fun openTransactionWinning(): NewWinningFragment {
        return NewWinningFragment.newInstance()
    }

    override fun openTransactionDetailBottomSheet(id: String) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/transactionDetailBottomSheet/$id"),
            getNavOptions(shouldAnimate = true)
        )
    }

}