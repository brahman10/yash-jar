package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Partnerships(
    @SerialName("headerText")
    val headerText: String? = null,
    @SerialName("headerTextNew")
    val headerTextNew: String? = null,
    @SerialName("headerIcon")
    val headerIcon: String? = null,
    @SerialName("partnershipCardsList")
    val partnershipCardsList: List<PartnershipCards?>? = null
)