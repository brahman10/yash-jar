package com.jar.app.feature_exit_survey.shared.domain.use_case.impl

import com.jar.app.feature_exit_survey.shared.data.repository.FeatureExitSurveyRepository
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchExitSurveyQuestionsUseCaseImpl constructor(
    private val repository: FeatureExitSurveyRepository
): FetchExitSurveyQuestionsUseCase {
    override suspend fun fetchExitSurveyQuestions(exitSurveyFor: String): Flow<RestClientResult<ApiResponseWrapper<ExitSurveyQuestions?>>> {
      return repository.fetchExitSurveyQuestions(exitSurveyFor)
    }
}