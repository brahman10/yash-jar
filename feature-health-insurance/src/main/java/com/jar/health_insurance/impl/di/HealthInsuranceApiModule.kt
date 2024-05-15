package com.jar.health_insurance.impl.di

import com.jar.health_insurance.api.HealthInsuranceApi
import com.jar.health_insurance.impl.HealthInsuranceApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class HealthInsuranceApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideSpendTrackerApi(healthInsuranceApiImpl: HealthInsuranceApiImpl): HealthInsuranceApi
}