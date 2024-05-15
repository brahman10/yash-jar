package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class GoalFirstQuestionResponse(
    @SerialName("charMaxLimit") val charMaxLimit: Int? = null,
    @SerialName("charMinLimit") val charMinLimit: Int? = null,
    @SerialName("footerButtonText") val footerButtonText: String? = null,
    @SerialName("goalRecommendedItems") val goalRecommendedItems: List<GoalRecommendedItem?>?,
    @SerialName("nameInputDefaultIcon") val nameInputDefaultIcon: String? = null,
    @SerialName("nameInputText") val nameInputText: String? = null,
    @SerialName("nameQuestion") val nameQuestion: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("defaultIconWithBottom") val defaultIconWithBottom: String? = null,
)

@kotlinx.serialization.Serializable
data class GoalRecommendedItem(
    @SerialName("bottomShadowImage") val bottomShadowImage: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("name") val name: String? = null,
    @Transient var isSelected: Boolean = false,
    @Transient var isCustomInput: Boolean = false,
)