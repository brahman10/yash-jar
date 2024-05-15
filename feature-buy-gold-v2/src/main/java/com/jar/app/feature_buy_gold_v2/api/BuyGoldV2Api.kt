package com.jar.app.feature_buy_gold_v2.api

import com.jar.app.core_base.util.BaseConstants

/**
 * Buy Gold V2 Api (to be used by other modules)
 * **/
interface BuyGoldV2Api {

    /**
     * Method to start setup buy-gold screen
     * ALWAYS USE buyGoldFlowContextFrom BaseConstants.BuyGoldFlowContext
     * **/
    fun openBuyGoldFlow(buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD)

    /**
     * Method to start setup buy-gold screen with Jackpot Coupon
     * ALWAYS USE buyGoldFlowContextFrom BaseConstants.BuyGoldFlowContext
     * **/
    fun openBuyGoldFlowWithCoupon(couponCode: String, couponType: String, isFromJackpotScreen: Boolean = false, buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD)

    /**
     * Method to start setup buy-gold screen with Jackpot Coupon
     * ALWAYS USE buyGoldFlowContextFrom BaseConstants.BuyGoldFlowContext
     * **/
    fun openBuyGoldFlowWithWeeklyChallengeAmount(amount: Float, buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD)

    /**
     * ALWAYS USE buyGoldFlowContextFrom BaseConstants.BuyGoldFlowContext
     * **/
    fun openBuyGoldFlowWithPrefillAmount(prefillAmount: Float, buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD)

    /**
     * Method to open screen after transaction completion to get the payment status
     * **/
    fun openOrderStatusFlow(
        transactionId: String,
        paymentProvider: String,
        paymentFlowSource: String,
        isOneTimeInvestment: Boolean,
        buyGoldFlowContext: String
    )

    /**
     * Method to show info dialog i.e, when we clicked on know-more on homepage card
     * **/
    fun openInfoDialog()

}