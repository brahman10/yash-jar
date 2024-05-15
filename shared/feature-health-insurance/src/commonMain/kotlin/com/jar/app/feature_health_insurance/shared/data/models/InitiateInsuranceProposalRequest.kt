package com.jar.app.feature_health_insurance.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitiateInsuranceProposalRequest(
    @SerialName("maxAge")
    val maxAge: Int,
    @SerialName("adultCnt")
    val adultCnt: Int,
    @SerialName("kidCnt")
    val kidCnt: Int
)