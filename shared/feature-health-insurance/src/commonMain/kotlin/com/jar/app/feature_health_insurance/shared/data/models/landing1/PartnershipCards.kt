package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnershipCards(
    @SerialName("header")
    val header: String?,
    @SerialName("icon")
    val icon: String?,
    @SerialName("subHeader")
    val subHeader: String?
)