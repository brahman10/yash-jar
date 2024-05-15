package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailySavingEducationResp(
    @SerialName("contentType")
    val contentType: String,
    @SerialName("dailySavingsStepsResponse")
    val dailySavingEducationData: DailySavingEducationData
)