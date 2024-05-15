package com.jar.app.feature.survey.domain.use_case

import com.jar.app.feature.survey.domain.model.SubmitSurveyResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.serialization.json.JsonElement

interface SubmitUserSurveyUseCase {
    suspend fun submitSurvey(jsonElement: JsonElement): RestClientResult<ApiResponseWrapper<SubmitSurveyResponse>>
}