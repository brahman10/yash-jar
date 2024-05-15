package com.jar.app.feature_round_off.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RoundOffSteps(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("stepsHeader")
    val stepsHeader: String? = null,
    @SerialName("stepsList")
    val stepsList: List<String>? = null,
    @SerialName("lottie")
    val lottie: String? = null,
    @SerialName("roundOffSetupFlowViewType")
    val roundOffSetupFlowViewType: String
)
