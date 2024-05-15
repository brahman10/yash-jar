package com.jar.app.feature_daily_investment.impl.di

import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.impl.data.DailyInvestmentApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class DailyInvestApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideDailyInvestmentApi(dailyInvestmentApiImpl: DailyInvestmentApiImpl): DailyInvestmentApi
}