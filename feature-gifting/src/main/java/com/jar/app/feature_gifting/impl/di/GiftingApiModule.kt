package com.jar.app.feature_gifting.impl.di

import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_gifting.impl.GiftingApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class GiftingApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideGiftingApi(giftingApiImpl: GiftingApiImpl): GiftingApi

}