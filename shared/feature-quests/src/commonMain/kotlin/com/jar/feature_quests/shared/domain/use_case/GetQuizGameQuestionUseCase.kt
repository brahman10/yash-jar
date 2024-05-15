package com.jar.feature_quests.shared.domain.use_case

import com.jar.feature_quests.shared.domain.model.QuestionAnswersData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface GetQuizGameQuestionUseCase {
    suspend fun getQuizGameQuestion(): Flow<RestClientResult<ApiResponseWrapper<QuestionAnswersData?>>>
}