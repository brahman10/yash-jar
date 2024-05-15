package com.jar.app.feature_goal_based_saving.impl.di

import com.jar.app.feature_goal_based_saving.api.GoalBasedSavingApi
import com.jar.app.feature_goal_based_saving.impl.GoalBasedSavingApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class GoalBasedSavingUiModule {
    @Binds
    @ActivityScoped
    abstract fun provideGoalBasedSavingApi(goalBasedSavingApiImpl: GoalBasedSavingApiImpl): GoalBasedSavingApi
}
