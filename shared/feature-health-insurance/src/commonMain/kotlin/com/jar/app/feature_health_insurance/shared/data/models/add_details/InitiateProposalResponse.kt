package com.jar.app.feature_health_insurance.shared.data.models.add_details

import kotlinx.serialization.Serializable

@Serializable
data class InitiateProposalResponse(
    val orderId: String
)