package com.jar.app.feature_daily_investment_cancellation.impl.di

import com.jar.app.feature_daily_investment_cancellation.api.impl.DailyInvestmentCancellationSettingsV2ApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class DailyInvestmentCancellationSettingsV2ApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideDailyInvestmentCancellationSettingsV2Api(dailyInvestmentCancellationSettingsV2ApiImpl: DailyInvestmentCancellationSettingsV2ApiImpl): com.jar.app.feature_daily_investment_cancellation.api.DailyInvestmentCancellationSettingsV2Api
}