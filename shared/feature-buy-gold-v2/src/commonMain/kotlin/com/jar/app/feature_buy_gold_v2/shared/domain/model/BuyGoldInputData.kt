package com.jar.app.feature_buy_gold_v2.shared.domain.model

@kotlinx.serialization.Serializable
data class BuyGoldInputData(
    val couponCode: String? = null,
    val couponType: String? = null,
    val challengeAmount: Float? = null,
    val showWeeklyChallengeAnimation: Boolean = false,
    val isFromJackpotScreen: Boolean = false,
    val prefilledAmountForFailedState: Float? = null,
    val prefillAmount: Float? = null
)