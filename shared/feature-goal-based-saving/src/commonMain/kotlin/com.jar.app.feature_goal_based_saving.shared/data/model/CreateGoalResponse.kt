package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreateGoalResponse(
    @SerialName("active") val active: Boolean? = null,
    @SerialName("goalId") val goalId: String? = null
)