package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SplashViewItems(
    @SerialName("splashCoin1")
    val splashCoin1: String?,
    @SerialName("splashCoin2")
    val splashCoin2: String?,
    @SerialName("splashCoin3")
    val splashCoin3: String?,
    @SerialName("splashCoin4")
    val splashCoin4: String?,
    @SerialName("splashCoin5")
    val splashCoin5: String?,
    @SerialName("splashCoin6")
    val splashCoin6: String?,
    @SerialName("splashCoin7")
    val splashCoin7: String?
)