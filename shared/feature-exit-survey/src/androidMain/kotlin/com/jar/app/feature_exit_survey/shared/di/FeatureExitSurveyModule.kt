package com.jar.app.feature_exit_survey.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_exit_survey.shared.data.network.FeatureExitSurveyDataSource
import com.jar.app.feature_exit_survey.shared.data.repository.FeatureExitSurveyRepository
import com.jar.app.feature_exit_survey.shared.domain.repository.ExitSurveyRepositoryImpl
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCaseImpl
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.PostExitSureveyUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.PostExitSureveyUseCaseImpk
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FeatureExitSurveyModule {

    @Provides
    @Singleton
    internal fun provideFeatureExitSurveyDataSource(@AppHttpClient client: HttpClient): FeatureExitSurveyDataSource {
        return FeatureExitSurveyDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideFeatureExitSurveyRepository(featureExitSurveyDataSource: FeatureExitSurveyDataSource): FeatureExitSurveyRepository {
        return ExitSurveyRepositoryImpl(
            featureExitSurveyDataSource
        )
    }


    @Provides
    @Singleton
        internal fun provideFetchExitSurveyQuestionsUseCase(featureExitSurveyRepository: FeatureExitSurveyRepository): FetchExitSurveyQuestionsUseCase {
        return FetchExitSurveyQuestionsUseCaseImpl(featureExitSurveyRepository)
    }

    @Provides
    @Singleton
    internal fun providePostExitSureveyUseCase(featureExitSurveyRepository: FeatureExitSurveyRepository): PostExitSureveyUseCase {
        return PostExitSureveyUseCaseImpk(featureExitSurveyRepository)
    }
}