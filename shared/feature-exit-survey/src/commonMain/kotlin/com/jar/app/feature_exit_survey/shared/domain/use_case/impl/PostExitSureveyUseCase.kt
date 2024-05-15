package com.jar.app.feature_exit_survey.shared.domain.use_case.impl

import com.jar.app.feature_exit_survey.shared.domain.model.SubmitExitSurveyResponseModel
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface PostExitSureveyUseCase {
    suspend fun postExitSurvey(reason: SubmitExitSurveyResponseModel): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}