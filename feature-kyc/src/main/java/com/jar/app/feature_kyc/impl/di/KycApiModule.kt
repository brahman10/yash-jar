package com.jar.app.feature_kyc.impl.di

import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_kyc.impl.data.KycApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class KycApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideKycApi(kycApiImpl: KycApiImpl): KycApi

}