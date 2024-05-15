package com.jar.app.feature_exit_survey.shared.domain.di

import com.jar.app.feature_exit_survey.shared.data.network.FeatureExitSurveyDataSource
import com.jar.app.feature_exit_survey.shared.data.repository.FeatureExitSurveyRepository
import com.jar.app.feature_exit_survey.shared.domain.repository.ExitSurveyRepositoryImpl
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCaseImpl
import io.ktor.client.HttpClient

class CommonExitSurveyModule (client: HttpClient) {

    val featureExitSurveyData: FeatureExitSurveyDataSource by lazy {
        FeatureExitSurveyDataSource(client)
    }
    val featureExitSurveyRepo: FeatureExitSurveyRepository by lazy {
        ExitSurveyRepositoryImpl(featureExitSurveyData)
    }

    val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase by lazy {
        FetchExitSurveyQuestionsUseCaseImpl(featureExitSurveyRepo)
    }

}