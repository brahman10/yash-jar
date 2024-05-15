package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreateGoalRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("amount") val amount: Long? = null,
    @SerialName("duration") val duration: Int? = null
)