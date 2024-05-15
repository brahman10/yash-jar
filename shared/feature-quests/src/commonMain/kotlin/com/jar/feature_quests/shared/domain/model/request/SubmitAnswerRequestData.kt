package com.jar.feature_quests.shared.domain.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class SubmitAnswerRequestData(
    @SerialName("questionId")
    val questionId: String,
    @SerialName("answeredOption")
    val answeredOption: String
)