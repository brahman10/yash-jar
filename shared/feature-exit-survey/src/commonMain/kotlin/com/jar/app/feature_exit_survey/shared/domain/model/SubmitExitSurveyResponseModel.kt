package com.jar.app.feature_exit_survey.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitExitSurveyResponseModel(
    @SerialName("featureSurveyId")
    val featureSurveyId: String,
    @SerialName("responses")
    val responses: List<String>
)
