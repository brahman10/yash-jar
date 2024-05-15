package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailySavingEducationData(
    @SerialName("header")
    val header:String,
    @SerialName("dailySavingsStepsList")
    val dailySavingEducation: List<DailySavingEducation>
)
