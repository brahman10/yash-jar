package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BenefitsX(
    @SerialName("icon")
    val icon: String?,
    @SerialName("text")
    val text: String?
)