package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalEndRequest(
    @SerialName("goalId")
    val goalId: String? = null,
    @SerialName("message")
    val message: String? = null
)