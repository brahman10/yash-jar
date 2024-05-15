package com.jar.app.feature_exit_survey.shared.domain.repository

import com.jar.app.feature_exit_survey.shared.data.network.FeatureExitSurveyDataSource
import com.jar.app.feature_exit_survey.shared.data.repository.FeatureExitSurveyRepository
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions
import com.jar.app.feature_exit_survey.shared.domain.model.SubmitExitSurveyResponseModel
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class ExitSurveyRepositoryImpl constructor(
    private val featureExitSurveyDataSource: FeatureExitSurveyDataSource
) : FeatureExitSurveyRepository {

    override suspend fun fetchExitSurveyQuestions(questionsFor: String): Flow<RestClientResult<ApiResponseWrapper<ExitSurveyQuestions?>>> {
        return getFlowResult {
            featureExitSurveyDataSource.fetchExitSurveyQuestions(questionsFor)
        }
    }

    override suspend fun postExitSurveyQuestion(requestBody: SubmitExitSurveyResponseModel): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> {
        return getFlowResult {
            featureExitSurveyDataSource.submitExitSurvey(requestBody)
        }
    }

}