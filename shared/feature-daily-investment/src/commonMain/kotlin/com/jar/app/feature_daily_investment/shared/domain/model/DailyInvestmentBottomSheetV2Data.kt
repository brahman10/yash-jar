package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailyInvestmentBottomSheetV2Data(
    @SerialName("contentType")
    val contentType: String,

    @SerialName("param")
    val param: Int,

    @SerialName("sphBottomSheetStaticData")
    val sphBottomSheetStaticData: SphBottomSheetStaticData
)

@kotlinx.serialization.Serializable
data class SphBottomSheetStaticData(
    @SerialName("buttonText")
    val buttonText: String,

    @SerialName("description")
    val description: String,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("title")
    val title: String
)