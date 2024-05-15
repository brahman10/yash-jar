package com.jar.feature_quests.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class QuestionAnswersData(
    @SerialName("answerOptions")
    val answerOptions: List<String?>? = null,
    @SerialName("footerChancesLeft")
    val footerChancesLeft: List<String>? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("questNumber")
    val questNumber: String? = null,
    @SerialName("questionId")
    val questionId: String? = null,
    @SerialName("questionText")
    val questionText: String? = null,
    @SerialName("toolbarText")
    val toolbarText: String? = null,
    @SerialName("quizViewItems")
    val quizViewItems: QuizViewItems? = null,
    @SerialName("chancesLeftCount")
    val chancesLeftCount: Int? = null
)