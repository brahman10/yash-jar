package com.jar.app.feature_round_off.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffStepsData(
    @SerialName("header")
    val header: String,
    @SerialName("roundOffStepsList")
    val roundOffSteps: List<RoundOffSteps>
)