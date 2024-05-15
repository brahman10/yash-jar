package com.jar.app.feature_buy_gold_v2.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BuyGoldBottomSheetV2Data(
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
    val icon: String,

    @SerialName("title")
    val title: String
)