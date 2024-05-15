package com.jar.app.feature_health_insurance.shared.data.models.add_details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitiateProposalRequest(
    @SerialName("maxAge")
    val maxAge: Int,
    @SerialName("adultCnt")
    val adultCount: Int,
    @SerialName("kidCnt")
    val kidCount: Int
)
