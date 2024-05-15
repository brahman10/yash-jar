package com.jar.feature_quests.shared.domain.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class WelcomeRewardData(
    @SerialName("landingViewItems")
    val landingViewItems: LandingViewItems?,
    @SerialName("questBanner")
    val questBanner: String?,
    @SerialName("splashViewItems")
    val splashViewItems: SplashViewItems?,
    @SerialName("toolbarText")
    val toolbarText: String?
)