package com.jar.app.feature_round_off.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffStepsResp(
    @SerialName("contentType")
    val contentType:String,
    @SerialName("roundOffStepsResponse")
    val roundOffStepsData: RoundOffStepsData
)
