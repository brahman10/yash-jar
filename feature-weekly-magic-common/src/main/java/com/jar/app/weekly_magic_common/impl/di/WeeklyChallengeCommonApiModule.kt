package com.jar.app.weekly_magic_common.impl.di

import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.weekly_magic_common.impl.WeeklyChallengeCommonApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class WeeklyChallengeCommonApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideWeeklyChallengeCommonApi(weeklyChallengeCommonApiImpl: WeeklyChallengeCommonApiImpl): WeeklyChallengeCommonApi

}