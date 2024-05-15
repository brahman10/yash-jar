package com.jar.app.feature_payment.impl.di

import `in`.juspay.services.HyperServices
import androidx.fragment.app.FragmentActivity
import com.jar.app.feature_payment.impl.PaymentManagerImpl
import com.jar.app.feature_payment.api.PaymentManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class PaymentManagerModule {

    companion object {
        @Provides
        @ActivityScoped
        fun provideHyperService(activity: FragmentActivity): HyperServices {
            return HyperServices(activity)
        }
    }

    @Binds
    @ActivityScoped
    internal abstract fun providePaymentManager(paymentManagerImpl: PaymentManagerImpl): PaymentManager

}