package com.jar.app.feature_gold_redemption.shared.data.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbandonScreenData(
    @SerialName("bulletImageUrl")
    val bulletImageUrl: String? = null,
    @SerialName("footerButtonText1")
    val footerButtonText1: String? = null,
    @SerialName("footerButtonText2")
    val footerButtonText2: String? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("foterButtonDeepLink1")
    val foterButtonDeepLink1: String? = null,
    @SerialName("foterButtonDeepLink2")
    val foterButtonDeepLink2: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("profileImages")
    val profileImages: List<String?>? = null,
    @SerialName("title1")
    val title1: String? = null,
    @SerialName("title2")
    val title2: String? = null
)