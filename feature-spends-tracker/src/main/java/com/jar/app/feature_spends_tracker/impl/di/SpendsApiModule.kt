package com.jar.app.feature_spends_tracker.impl.di

import com.jar.app.feature_spends_tracker.api.SpendsTrackerApi
import com.jar.app.feature_spends_tracker.impl.SpendTrackerApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class SpendsApiModule {
      @Binds
      @ActivityScoped
      internal abstract fun provideSpendTrackerApi(spendTrackerApiImpl: SpendTrackerApiImpl): SpendsTrackerApi
}