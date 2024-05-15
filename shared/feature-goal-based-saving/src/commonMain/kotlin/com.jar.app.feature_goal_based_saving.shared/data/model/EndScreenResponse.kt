package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EndScreenResponse(
    @SerialName("header")
    val header: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("continueButton")
    val continueButton: Button? = null,
    @SerialName("endGoalButton")
    val endGoalButton: Button? = null
)

