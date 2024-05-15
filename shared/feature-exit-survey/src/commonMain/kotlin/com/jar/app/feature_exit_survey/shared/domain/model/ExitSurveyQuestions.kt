package com.jar.app.feature_exit_survey.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ExitSurveyQuestions(
    @SerialName("featureSurveyId")
    val featureSurveyId: String,
    @SerialName("question")
    val question: String,
    @SerialName("choices")
    val choices: List<Choice?>? = null,
    @SerialName("textBoxHint")
    val textBoxHint: String? = null,
    @SerialName("submitButton")
    val submitButton: Button? = null,
    @SerialName("button2Cta")
    val button2Cta: Button2Cta? = null,
    @SerialName("mcq")
    val mcq: Boolean? = null
)

@Serializable
data class Choice(
    @SerialName("text")
    var text: String,
    @SerialName("editable")
    var editable: Boolean? = null,
    @Transient
    var otherOptionText: String? = null,
    @Transient
    var isSelected: Boolean = false
)

@Serializable
data class Button(
    @SerialName("text")
    val text: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null,

)

@Serializable
data class Button2Cta(
    @SerialName("text")
    val text: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deeplink")
    val deeplink: String
)
