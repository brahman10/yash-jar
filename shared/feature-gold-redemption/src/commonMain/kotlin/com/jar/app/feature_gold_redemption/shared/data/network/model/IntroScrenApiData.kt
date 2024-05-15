package com.jar.app.feature_gold_redemption.shared.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntroScrenApiData(
    @SerialName("brandPartnersImageList")
    val brandPartnersImageList: List<String?>? = null,
    @SerialName("brandPartnersTitle")
    val brandPartnersTitle: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("goldDiamondTableImage")
    val goldDiamondTableImage: String? = null,
    @SerialName("myOrdersText")
    val myVouchersText: String? = null,
)