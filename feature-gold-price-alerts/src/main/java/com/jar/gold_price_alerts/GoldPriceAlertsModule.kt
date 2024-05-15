package com.jar.gold_price_alerts

import com.jar.gold_price_alerts.api.GoldPriceAlertsApi
import com.jar.gold_price_alerts.impl.data.GoldPriceAlertsApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class CoreUiApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideGoldPriceAlertsApi(goldPriceAlertsApiImpl: GoldPriceAlertsApiImpl): GoldPriceAlertsApi
}