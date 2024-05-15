package com.jar.app.feature_savings_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateUserSavingRequest(
    @SerialName("subscriptionAmount")
    val subscriptionAmount: Int? = null,
    @SerialName("subscriptionDay")
    val subscriptionDay: Int? = null,
    @SerialName("subscriptionType")
    val subscriptionType: String? = null,   // possible values -> WEEKLY_SIP, MONTHLY_SIP, DEFAULT(DS)
    @SerialName("savingsType")
    val savingsType: String,                //possible values -> GOLD_SIPS, DAILY_SAVINGS, ROUND_OFFS
    @SerialName("roundOffTo")
    val roundOffTo: String? = null,         //possible values -> NEAREST_TEN, NEAREST_FIVE
    @SerialName("autoInvestForNoSpends")
    val autoInvestForNoSpends: Boolean? = null
)
