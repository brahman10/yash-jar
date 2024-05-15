package com.jar.feature_quests.shared.domain.use_case

import com.jar.feature_quests.shared.domain.model.SubmitAnswerData
import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface MarkAnswerUseCase {
    suspend fun markAnswer(body: SubmitAnswerRequestData): Flow<RestClientResult<ApiResponseWrapper<SubmitAnswerData?>>>
}
