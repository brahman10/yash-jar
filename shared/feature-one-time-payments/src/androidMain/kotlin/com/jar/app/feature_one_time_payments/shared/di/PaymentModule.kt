package com.jar.app.feature_one_time_payments.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_one_time_payments.shared.data.network.PaymentDataSource
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.repository.PaymentRepositoryImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.*
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class PaymentModule {

    @Provides
    @Singleton
    internal fun providePaymentDataSource(@AppHttpClient client: HttpClient): PaymentDataSource {
        return PaymentDataSource(client)
    }

    @Provides
    @Singleton
    internal fun providePaymentRepository(paymentDataSource: PaymentDataSource): PaymentRepository {
        return PaymentRepositoryImpl(paymentDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchManualPaymentStatusUseCase(paymentRepository: PaymentRepository): FetchManualPaymentStatusUseCase {
        return FetchManualPaymentStatusUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideVerifyUpiAddressUseCase(paymentRepository: PaymentRepository): VerifyUpiAddressUseCase {
        return VerifyUpiAddressUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateUpiCollectUseCase(paymentRepository: PaymentRepository): InitiateUpiCollectUseCase {
        return InitiateUpiCollectUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideRetryPaymentUseCase(paymentRepository: PaymentRepository): RetryPaymentUseCase {
        return RetryPaymentUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideCancelPaymentUseCase(paymentRepository: PaymentRepository): CancelPaymentUseCase {
        return CancelPaymentUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRecentlyUsedPaymentMethodsUseCase(
        paymentRepository: PaymentRepository,
        json: Json
    ): FetchRecentlyUsedPaymentMethodsUseCase {
        return FetchRecentlyUsedPaymentMethodsUseCaseImpl(paymentRepository, json)
    }

    @Provides
    @Singleton
    internal fun provideFetchSavedUpiIdUseCase(paymentRepository: PaymentRepository): FetchSavedUpiIdUseCase {
        return FetchSavedUpiIdUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchEnabledPaymentMethodUseCase(paymentRepository: PaymentRepository): FetchEnabledPaymentMethodUseCase {
        return FetchEnabledPaymentMethodUseCaseImpl(paymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideOrderStatusDynamicCardsUseCase(paymentRepository: PaymentRepository): FetchOrderStatusDynamicCardsUseCase {
        return FetchOrderStatusDynamicCardsUseCaseImpl(paymentRepository)
    }
}