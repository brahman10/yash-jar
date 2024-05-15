package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyInvestmentIntroData(
    @SerialName("param")
    val param: Int,

    @SerialName("contentType")
    val contentType: String,

    @SerialName("dailySavingsAbandonedStepsResponse")
    val dailySavingIntroScreen: DailySavingIntroScreen

)

@Serializable
data class DailySavingIntroScreen(

    @SerialName("header")
    val header: String,

    @SerialName("dailySavingsStepsList")
    val dailySavingsStepsList: ArrayList<Steps>? = null
)
