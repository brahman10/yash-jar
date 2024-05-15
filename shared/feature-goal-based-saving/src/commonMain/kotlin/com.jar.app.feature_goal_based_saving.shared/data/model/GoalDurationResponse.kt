package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class GoalDurationResponse(
    @SerialName("answeredDetails")
    val answeredDetails: List<AnsweredDetail?>?,
    @SerialName("automateGoalText")
    val automateGoalText: String?,
    @SerialName("goalRecommendedTimes")
    val goalRecommendedTimes: List<GoalRecommendedTime?>?,
    @SerialName("timeQuestion")
    val timeQuestion: String?,
    @SerialName("title")
    val title: String?
)

@kotlinx.serialization.Serializable
data class GoalRecommendedTime(
    @SerialName("monthText")
    val monthText: String?,
    @SerialName("number")
    val number: Int?,
    var isSelected: Boolean = false
)