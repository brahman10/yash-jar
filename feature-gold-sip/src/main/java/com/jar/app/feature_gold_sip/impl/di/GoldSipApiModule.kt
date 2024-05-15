package com.jar.app.feature_gold_sip.impl.di

import com.jar.app.feature_gold_sip.api.GoldSipApi
import com.jar.app.feature_gold_sip.impl.GoldSipApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class GoldSipApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideGoldSipApi(goldSipApiImpl: GoldSipApiImpl): GoldSipApi

}