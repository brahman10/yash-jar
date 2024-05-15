package com.jar.app.feature_exit_survey.shared.data.network

import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions
import com.jar.app.feature_exit_survey.shared.domain.model.SubmitExitSurveyResponseModel
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class FeatureExitSurveyDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchExitSurveyQuestions(exitSurveyFor: String) =
        getResult<ApiResponseWrapper<ExitSurveyQuestions?>> {
            client.get {
                url("/v1/api/feature/survey/fetch")
                parameter("surveyType", exitSurveyFor)
            }
        }

    suspend fun submitExitSurvey(requestBody: SubmitExitSurveyResponseModel) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url("/v1/api/feature/survey/response")
                setBody(requestBody)
            }
        }

}