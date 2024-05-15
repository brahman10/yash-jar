package com.myjar.app.feature_exit_survey.impl.di

import com.myjar.app.feature_exit_survey.api.ExitSurveyApi
import com.myjar.app.feature_exit_survey.impl.ExitSurveyApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal interface ExitSurveyApiModule {
    @Binds
    @ActivityScoped
    fun provideExitSurvey(exitSurveyApiImpl: ExitSurveyApiImpl) : ExitSurveyApi
}