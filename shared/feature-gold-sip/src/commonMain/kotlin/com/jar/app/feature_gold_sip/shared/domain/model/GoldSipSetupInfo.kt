package com.jar.app.feature_gold_sip.shared.domain.model

import com.jar.app.feature_user_api.domain.model.SuggestedAmount
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldSipSetupInfo(
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
    val options: List<SuggestedAmount>
)