package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QuestionAnswer(
    @SerialName("answer")
    val answer: String? = null,
    @SerialName("question")
    val question: String? = null
)