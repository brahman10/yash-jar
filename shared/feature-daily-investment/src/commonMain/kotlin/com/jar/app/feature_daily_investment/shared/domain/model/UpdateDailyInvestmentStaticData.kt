package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class UpdateDailyInvestmentStaticData(
    @SerialName("title")
    val title: String? = null,

    @SerialName("text")
    val text: String? = null,

    @SerialName("subText")
    val subText: String? = null,

    @SerialName("editAmountButtonText")
    val editAmountButtonText: String? = null,

    @SerialName("paymentButtonText")
    val paymentButtonText: String? = null,

    @SerialName("backgroundImagw")
    val backgroundImagw: String? = null,

    @SerialName("dsActiveAmount")
    val currentDailySavingsAmount: Float? = null,

    @SerialName("dsRecommendedAmount")
    val dsRecommendedAmount: Float? = null,

    @SerialName("currentSavings")
    val currentSavings: GenericUpdateDailyInvestmentData? = null,

    @SerialName("recommendedSavings")
    val recommendedSavings: GenericUpdateDailyInvestmentData? = null,

    @SerialName("savingsDuration")
    val savingsDuration: String? = null,
){
    @kotlinx.serialization.Serializable
    data class GenericUpdateDailyInvestmentData(
        @SerialName("header")
        val header: String? = null,

        @SerialName("text")
        val text: String? = null,

        @SerialName("amount")
        val amount: String? = null,

        @SerialName("image")
        val image: String? = null,
    )
}

