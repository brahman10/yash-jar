package com.jar.app.feature.survey.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SurveyQuestion(
    @SerialName("id")
    val id: String,
    @SerialName("surveyId")
    val surveyId: String,
    @SerialName("question")
    val question: String,
    @SerialName("choices")
    val choices: List<String>,
    @SerialName("mcq")
    val mcq: Boolean
) : Parcelable

data class ChoiceWrapper(
    @SerialName("choice")
    val choice: String,

    @SerialName("isSelected")
    var isSelected: Boolean = false
)
