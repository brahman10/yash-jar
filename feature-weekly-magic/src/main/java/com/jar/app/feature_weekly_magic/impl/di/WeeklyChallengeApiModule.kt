package com.jar.app.feature_weekly_magic.impl.di

import com.jar.app.feature_weekly_magic.api.WeeklyChallengeApi
import com.jar.app.feature_weekly_magic.impl.WeeklyChallengeApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class WeeklyChallengeApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideWeeklyChallengeApi(weeklyChallengeApiImpl: WeeklyChallengeApiImpl): WeeklyChallengeApi

}