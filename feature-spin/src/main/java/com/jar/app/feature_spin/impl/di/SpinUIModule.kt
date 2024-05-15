package com.jar.app.feature_spin.impl.di

import com.jar.app.feature_spin.api.SpinApi
import com.jar.app.feature_spin.impl.SpinApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class SpinUIModule {

    @Binds
    @ActivityScoped
    abstract fun provideSpinApi(spinApiImpl: SpinApiImpl): SpinApi

}