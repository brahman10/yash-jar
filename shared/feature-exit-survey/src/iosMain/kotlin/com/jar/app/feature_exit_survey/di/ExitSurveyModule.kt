package com.jar.app.feture_exit_survey.di


import com.jar.app.feature_exit_survey.shared.data.network.FeatureExitSurveyDataSource
import com.jar.app.feature_exit_survey.shared.data.repository.FeatureExitSurveyRepository
import com.jar.app.feature_exit_survey.shared.domain.repository.ExitSurveyRepositoryImpl
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCaseImpl
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.PostExitSureveyUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.PostExitSureveyUseCaseImpk
import io.ktor.client.HttpClient


class ExitSurveyModule(
    client: HttpClient
) {
    private val featureExitSurveyDataSource: FeatureExitSurveyDataSource by lazy {
        FeatureExitSurveyDataSource(client)
    }
    private val featureExitSurveyRepository: FeatureExitSurveyRepository by lazy {
        ExitSurveyRepositoryImpl(
            featureExitSurveyDataSource
        )
    }

    val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase by lazy {
        FetchExitSurveyQuestionsUseCaseImpl(featureExitSurveyRepository)
    }
    val postExitSurveyUseCase: PostExitSureveyUseCase by lazy {
        PostExitSureveyUseCaseImpk(featureExitSurveyRepository)
    }
}