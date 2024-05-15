package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FirstCoinHomeScreenData(

    @SerialName("header")
    val header: String,

    @SerialName("percentageCompleted")
    val percentageCompleted: Float,

    @SerialName("iconUrl")
    val iconUrl: String,

    @SerialName("onboarded")
    val onboarded: Boolean,

    @SerialName("deliveryOrderId")
    val deliveryOrderId: String? = null,

    @SerialName("trackingLink")
    val trackingLink: String? = null,

    @SerialName("deliveryStatus")
    val deliveryStatus: String? = null
)