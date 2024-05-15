package com.jar.app.feature_health_insurance.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitiateInsurancePlanResponse(
    @SerialName("orderId")
    val orderId: String
)