package com.jar.app.feature_mandate_payment.impl.di

import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.data.MandatePaymentApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class MandatePaymentApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideMandatePaymentApi(mandatePaymentApiImpl: MandatePaymentApiImpl): MandatePaymentApi

}