package com.jar.app.feature_calculator.impl.di

import com.jar.app.feature_calculator.api.CalculatorApi
import com.jar.app.feature_calculator.impl.CalculatorApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class CalculatorApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideCalculatorApi(calculatorApiImpl: CalculatorApiImpl): CalculatorApi

}