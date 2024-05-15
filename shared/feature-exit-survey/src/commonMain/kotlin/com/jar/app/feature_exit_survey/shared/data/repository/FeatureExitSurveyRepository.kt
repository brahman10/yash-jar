package com.jar.app.feature_exit_survey.shared.data.repository

import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions
import com.jar.app.feature_exit_survey.shared.domain.model.SubmitExitSurveyResponseModel
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FeatureExitSurveyRepository : BaseRepository {

    suspend fun fetchExitSurveyQuestions(questionsFor: String): Flow<RestClientResult<ApiResponseWrapper<ExitSurveyQuestions?>>>

    suspend fun postExitSurveyQuestion(requestBody: SubmitExitSurveyResponseModel): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}