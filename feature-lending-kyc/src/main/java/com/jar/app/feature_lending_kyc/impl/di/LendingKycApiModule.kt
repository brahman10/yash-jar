package com.jar.app.feature_lending_kyc.impl.di

import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.feature_lending_kyc.impl.data.LendingKycApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class LendingKycApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideLendingKycApi(lendingKycApiImpl: LendingKycApiImpl): LendingKycApi
}