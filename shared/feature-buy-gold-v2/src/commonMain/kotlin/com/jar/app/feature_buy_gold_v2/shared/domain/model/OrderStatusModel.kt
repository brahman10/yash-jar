package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.core_base.util.BaseConstants

@kotlinx.serialization.Serializable
data class OrderStatusModel (
    val transactionId: String,
    val paymentProvider: String,
    val  paymentFlowSource: String,
    val isOneTimeInvestment: Boolean,
    val buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD
) {
    fun isFromBuyGoldFlow() = paymentFlowSource == BaseConstants.ManualPaymentFlowType.BUY_GOLD || paymentFlowSource == BaseConstants.ManualPaymentFlowType.BUY_GOLD_SHUBH_MUHURAT || paymentFlowSource == BaseConstants.ManualPaymentFlowType.WEEKLY_MAGIC_FEATURE_SCREEN
}