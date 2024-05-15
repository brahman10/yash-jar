package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseRiskFactor(
    @SerialName("riskFactors")
    val riskFactors: String
)