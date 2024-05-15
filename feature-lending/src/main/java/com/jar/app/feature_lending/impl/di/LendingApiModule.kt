package com.jar.app.feature_lending.impl.di

import com.jar.app.feature_lending.api.LendingApi
import com.jar.app.feature_lending.impl.data.LendingApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class LendingApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideLendingApi(lendingApiImpl: LendingApiImpl): LendingApi

}