package com.jar.app.feature_sms_sync.impl.di

import com.jar.app.feature_sms_sync.api.SmsSyncApi
import com.jar.app.feature_sms_sync.impl.SmsSyncApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class SmsSyncApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideSmsSyncApi(smsSyncApiImpl: SmsSyncApiImpl): SmsSyncApi

}