package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SuggestedAmountOptions(
    @SerialName("prefillAmount")
    val prefillAmount: Float? = null,
    @SerialName("options")
    val options: List<SuggestedAmount>,
    @SerialName("volumeOptions")
    val volumeOptions: List<SuggestedAmount>,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("weeklyChallengeMinimumAmount")
    val weeklyChallengeMinimumAmount: Float? = null,
    @SerialName("weeklyChallengeEligibleText")
    val weeklyChallengeEligibleText: String? = null,
    @SerialName("weeklyChallengeNotEligibleText")
    val weeklyChallengeNotEligibleText: String? = null,
    @SerialName("weeklyChallengeIcon")
    val weeklyChallengeIcon: String? = null,
    @SerialName("socialProofText")
    val socialProofText: String? = null,
    @SerialName("socialProofIcon")
    val socialProofIcon: String? = null,
    @SerialName("paymentMethodsInfo")
    val paymentMethodsInfo: BuyGoldPaymentMethodsInfo? = null,
    @SerialName("firstOtpTxnMinEligibleAmount")
    val firstOtpTxnMinEligibleAmount: Float? = null
)

@kotlinx.serialization.Serializable
data class BuyGoldPaymentMethodsInfo(
    @SerialName("primaryHeaderText")
    val primaryHeaderText: String? = null,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("paymentMethodsMaxCount")
    val paymentMethodsMaxCount: Int? = null,
    @SerialName("shouldShowPaymentMethods")
    val shouldShowPaymentMethods: Boolean? = null,
)