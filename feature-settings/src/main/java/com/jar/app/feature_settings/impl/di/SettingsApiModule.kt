package com.jar.app.feature_settings.impl.di

import com.jar.app.feature_settings.api.SettingsApi
import com.jar.app.feature_settings.api.SettingsApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class SettingsApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideSettingsApi(settingsApiImpl: SettingsApiImpl): SettingsApi

}