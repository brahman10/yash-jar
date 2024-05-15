package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingFooter(
    @SerialName("headerText")
    val headerText: String?,
    @SerialName("icon")
    val icon: String?,
    @SerialName("subHeaderText")
    val subHeaderText: String?
)