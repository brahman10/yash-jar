package com.jar.app.feature.survey.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserSurveyResponses(
    @SerialName("surveyId")
    var surveyId: String,

    @SerialName("responses")
    var responses: List<UserChoice>
)

@kotlinx.serialization.Serializable
data class UserChoice(
    @SerialName("questionId")
    var questionId: String,

    @SerialName("choicesSelected")
    var choicesSelected: List<Int>
)

@kotlinx.serialization.Serializable
data class SubmitSurveyResponse(
    @SerialName("title")
    val title: String,

    @SerialName("subTitle")
    val subTitle: String
)