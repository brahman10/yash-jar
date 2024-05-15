package com.jar.app.feature_round_off.impl.di

import com.jar.app.feature_round_off.api.RoundOffApi
import com.jar.app.feature_round_off.impl.RoundOffApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class RoundOffApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideRoundOffApi(roundOffApiImpl: RoundOffApiImpl): RoundOffApi

}
