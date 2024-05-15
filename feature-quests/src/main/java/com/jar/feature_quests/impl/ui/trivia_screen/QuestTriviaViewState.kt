package com.jar.feature_quests.impl.ui.trivia_screen

import com.jar.feature_quests.shared.domain.model.QuestionAnswersData
import com.jar.feature_quests.shared.domain.model.SubmitAnswerData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

data class QuestTriviaViewState(
    val questionAnswersData: RestClientResult<ApiResponseWrapper<QuestionAnswersData?>> = RestClientResult.none(),
    val submitAnswerData: RestClientResult<ApiResponseWrapper<SubmitAnswerData?>> = RestClientResult.none(),
    val optionSelectedIndex: Int? = null,
    val errorMessage: String? = null,
)