package com.jar.feature_quests.shared.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingViewItems(
    @SerialName("bottomSheetDesc")
    val bottomSheetDesc: String?,
    @SerialName("bottomSheetImage")
    val bottomSheetImage: String?,
    @SerialName("bottomSheetSliderText")
    val bottomSheetSliderText: String?,
    @SerialName("bottomSheetTitle")
    val bottomSheetTitle: String?,
    @SerialName("landingCoin1")
    val landingCoin1: String?,
    @SerialName("landingCoin2")
    val landingCoin2: String?,
    @SerialName("landingCoin3")
    val landingCoin3: String?,
    @SerialName("landingCoin4")
    val landingCoin4: String?,
    @SerialName("textAboveBanner")
    val textAboveBanner: String?,
    @SerialName("textBelowBanner")
    val textBelowBanner: String?
)