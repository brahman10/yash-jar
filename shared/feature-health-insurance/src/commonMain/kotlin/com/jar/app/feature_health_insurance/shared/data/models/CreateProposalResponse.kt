package com.jar.app.feature_health_insurance.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProposalResponse(
    @SerialName("insuranceId")
    val insuranceId: String? = null
)