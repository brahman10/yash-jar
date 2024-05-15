package com.jar.app.feature_savings_common.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavingSetupInfo(
    @SerialName("eligibleForSip")
    val eligibleForSip: Boolean,
    @SerialName("recommendedDay")
    val recommendedDay: Int,
    @SerialName("recommendedSubscriptionAmount")
    val recommendedSubscriptionAmount: Float,
    @SerialName("sliderMaxValue")
    val sliderMaxValue: Float,
    @SerialName("sliderMinValue")
    val sliderMinValue: Float,
    @SerialName("sliderStepCount")
    val sliderStepCount: Int,
    @SerialName("sliderStepValue")
    val sliderStepValue: Int,
    @SerialName("subscriptionType")
    val subscriptionType: String,
    @SerialName("options")
    val options: List<SavingSuggestedAmount>
)
