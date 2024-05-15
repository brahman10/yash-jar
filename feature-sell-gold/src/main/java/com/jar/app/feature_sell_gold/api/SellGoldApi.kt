package com.jar.app.feature_sell_gold.api

/**
 * Sell Gold Api (to be used by other modules)
 * **/
interface SellGoldApi {
    /**
     * Method to start setup sell-gold screen
     * **/
    fun openSellGoldFlow()

    /**
     * Method to open Withdraw bottom sheet if the user doesn't have anything to withdraw
     * **/
    fun openWithdrawBottomSheet()

    fun openVpaSelectionFragment(
        isRetryFlow: Boolean,
        withdrawalPrice: String?,
        orderId: String?,
        popUpTo: Int? = null
    )
}