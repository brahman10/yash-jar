package com.jar.app.feature_health_insurance.shared.data.models.plan_comparison


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanComparisonResponse(
    @SerialName("metadata")
    val metadata: Map<String, String>,
    @SerialName("Plans")
    val plans: Map<String, Map<String, String?>>
)
