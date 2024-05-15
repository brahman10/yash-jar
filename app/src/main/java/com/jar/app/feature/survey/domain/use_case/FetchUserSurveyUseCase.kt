package com.jar.app.feature.survey.domain.use_case

import com.jar.app.feature.survey.domain.model.Survey
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchUserSurveyUseCase {
    suspend fun fetchUserSurvey(): RestClientResult<ApiResponseWrapper<Survey?>>
}