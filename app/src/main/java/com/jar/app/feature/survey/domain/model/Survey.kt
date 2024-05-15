package com.jar.app.feature.survey.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class Survey(
    @SerialName("surveyId")
    val surveyId: String,
    @SerialName("surveyQuestions")
    val surveyQuestions: List<SurveyQuestion>? = null
) : Parcelable
