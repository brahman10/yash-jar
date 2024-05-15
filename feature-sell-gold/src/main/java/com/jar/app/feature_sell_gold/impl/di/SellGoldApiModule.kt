package com.jar.app.feature_sell_gold.impl.di

import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_sell_gold.impl.SellGoldApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class SellGoldApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideSellGoldApi(sellGoldApiImpl: SellGoldApiImpl): SellGoldApi

}