package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//
@Serializable
data class GoalEndResponse(
    @SerialName("goalStatus")
    val goalStatus: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("goalImage")
    val goalImage: String? = null,
    @SerialName("goalName")
    val goalName: String? = null,
    @SerialName("lottie")
    val lottie: String? = null,
    @SerialName("investmentHeader")
    val investmentHeader: String? = null,
    @SerialName("investedAmount")
    val investedAmount: String? = null,
    @SerialName("timeDesc")
    val timeDesc: String? = null,
    @SerialName("newGoalButton")
    val newGoalButton: Button? = null,
    @SerialName("withdrawButton")
    val withdrawButton: Button? = null
)

@Serializable
data class Button(
    @SerialName("text")
    val text: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)