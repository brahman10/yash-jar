package com.jar.app.feature_homepage.shared.domain.model.update_daily_saving

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdateDailySavingData(

    @SerialName("dailySavingsAmount")
    val currentDailySavingAmount: Float,

    @SerialName("recommendedDailySavingsAmount")
    val recommendedDailySavingsAmount: Float,

    @SerialName("eligibleForDSUpdate")
    val eligibleForDSUpdate: Boolean,

    @SerialName("sliderMinValue")
    val sliderMinValue: Float,

    @SerialName("sliderMaxValue")
    val sliderMaxValue: Float,

    @SerialName("sliderStepCount")
    val sliderStepValue: Int? = null
)