package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoalSavingsIntoPage(
     @SerialName("param")
    val param: Int,
     @SerialName("contentType")
    val contentType: String,
     @SerialName("goalSavingsIntroPageResponse")
    val goalSavingsIntroPageResponse: GoalSavingsIntroPageResponse
)

@kotlinx.serialization.Serializable
data class GoalSavingsIntroPageResponse(
     @SerialName("header1")
    val header1: String,
     @SerialName("header2")
    val header2: String,
     @SerialName("displayUrl")
    val displayUrl: String,
     @SerialName("footerButtonText")
    val footerButtonText: String
)
