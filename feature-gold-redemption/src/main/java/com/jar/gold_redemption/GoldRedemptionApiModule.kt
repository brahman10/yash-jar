package com.jar.gold_redemption

import com.jar.gold_redemption.api.GoldRedemptionApi
import com.jar.gold_redemption.impl.data.GoldRedemptionApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class GoldRedemptionApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideGoldRedemptionApi(goldRedemptionApiImpl: GoldRedemptionApiImpl): GoldRedemptionApi
}