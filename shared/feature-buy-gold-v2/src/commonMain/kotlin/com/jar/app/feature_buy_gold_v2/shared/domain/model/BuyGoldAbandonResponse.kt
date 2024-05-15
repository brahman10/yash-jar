package com.jar.app.feature_buy_gold_v2.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BuyGoldAbandonResponse(
    @SerialName("param")
    val param: Int,

    @SerialName("contentType")
    val contentType: String,

    @SerialName("buyGoldOnboardingAbandonScreen")
    val buyGoldAbandonScreen: BuyGoldAbandonScreen
)

@kotlinx.serialization.Serializable
data class BuyGoldAbandonScreen(
    @SerialName("header")
    val header: String? = null,

    @SerialName("stepsList")
    val stepsList: ArrayList<BuyGoldAbandonSteps>? = null,

    @SerialName("footerButton1")
    val footerButton1: String? = null,

    @SerialName("footerButton2")
    val footerButton2: String? = null,

    @SerialName("profilePics")
    val profilePics: ArrayList<String>? = null,

    @SerialName("footerText")
    val footerText: String? = null,
)

@kotlinx.serialization.Serializable
data class BuyGoldAbandonSteps(
    @SerialName("text")
    val title: String,

    @SerialName("iconUrl")
    val imageUrl: String? = null,
)