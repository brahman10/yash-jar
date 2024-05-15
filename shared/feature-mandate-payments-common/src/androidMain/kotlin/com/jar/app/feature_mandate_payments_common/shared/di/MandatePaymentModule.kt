package com.jar.app.feature_mandate_payments_common.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_mandate_payments_common.shared.data.network.MandatePaymentDataSource
import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.repository.MandatePaymentRepositoryImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchEnabledPaymentMethodsUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandateEducationUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchPreferredBankUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchRecentlyUsedPaymentMethodUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchEnabledPaymentMethodsUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchMandateEducationUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchMandatePaymentStatusUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchPreferredBankUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchRecentlyUsedPaymentMethodUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.InitiateMandatePaymentUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.VerifyUpiAddressUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class MandatePaymentModule {

    @Provides
    @Singleton
    internal fun provideManualPaymentDataSource(@AppHttpClient client: HttpClient): MandatePaymentDataSource {
        return MandatePaymentDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideManualPaymentRepository(mandatePaymentDataSource: MandatePaymentDataSource): MandatePaymentRepository {
        return MandatePaymentRepositoryImpl(mandatePaymentDataSource)
    }

    @Provides
    @Singleton
    internal fun provideVerifyUpiAddressUseCase(mandatePaymentRepository: MandatePaymentRepository): VerifyUpiAddressUseCase {
        return VerifyUpiAddressUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateMandatePaymentUseCase(mandatePaymentRepository: MandatePaymentRepository): InitiateMandatePaymentUseCase {
        return InitiateMandatePaymentUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchMandatePaymentStatusUseCase(mandatePaymentRepository: MandatePaymentRepository): FetchMandatePaymentStatusUseCase {
        return FetchMandatePaymentStatusUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchMandateEducationUseCase(mandatePaymentRepository: MandatePaymentRepository): FetchMandateEducationUseCase {
        return FetchMandateEducationUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPreferredBankUseCase(mandatePaymentRepository: MandatePaymentRepository): FetchPreferredBankUseCase {
        return FetchPreferredBankUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchEnabledMethodsUseCase(mandatePaymentRepository: MandatePaymentRepository): FetchEnabledPaymentMethodsUseCase {
        return FetchEnabledPaymentMethodsUseCaseImpl(mandatePaymentRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRecentlyUsedPaymentMethodUseCase(
        mandatePaymentRepository: MandatePaymentRepository,
        json: Json
    ): FetchRecentlyUsedPaymentMethodUseCase {
        return FetchRecentlyUsedPaymentMethodUseCaseImpl(mandatePaymentRepository, json)
    }

}