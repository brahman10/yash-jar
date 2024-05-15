package com.jar.app.feature_jar_duo.impl.di

import com.jar.app.feature_jar_duo.api.DuoApi
import com.jar.app.feature_jar_duo.impl.DuoApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class DuoApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideDuoApi(duoApiImpl: DuoApiImpl): DuoApi

}