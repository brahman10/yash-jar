package com.jar.app.feature_buy_gold_v2.impl.di

import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2ApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class BuyGoldV2ApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideBuyGoldV2Api(buyGoldV2ApiImpl: BuyGoldV2ApiImpl): BuyGoldV2Api

}