package com.jar.app.feature_one_time_payments.shared.di

import com.jar.app.feature_one_time_payments.shared.data.network.PaymentDataSource
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.repository.PaymentRepositoryImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.CancelPaymentUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchSavedUpiIdUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.InitiateUpiCollectUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.RetryPaymentUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.CancelPaymentUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.FetchEnabledPaymentMethodUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.FetchManualPaymentStatusUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.FetchOrderStatusDynamicCardsUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.FetchRecentlyUsedPaymentMethodsUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.FetchSavedUpiIdUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.InitiateUpiCollectUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.RetryPaymentUseCaseImpl
import com.jar.app.feature_one_time_payments.shared.domain.use_case.impl.VerifyUpiAddressUseCaseImpl
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

class PaymentModule(
    client: HttpClient
) {

    private val paymentDataSource: PaymentDataSource by lazy {
        PaymentDataSource(client)
    }

    private val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(paymentDataSource)
    }

    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase by lazy {
        FetchManualPaymentStatusUseCaseImpl(paymentRepository)
    }

    val verifyUpiAddressUseCase: VerifyUpiAddressUseCase by lazy {
        VerifyUpiAddressUseCaseImpl(paymentRepository)
    }

    val initiateUpiCollectUseCase: InitiateUpiCollectUseCase by lazy {
        InitiateUpiCollectUseCaseImpl(paymentRepository)
    }

    val retryPaymentUseCase: RetryPaymentUseCase by lazy {
        RetryPaymentUseCaseImpl(paymentRepository)
    }

    val cancelPaymentUseCase: CancelPaymentUseCase by lazy {
        CancelPaymentUseCaseImpl(paymentRepository)
    }

    val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase by lazy {
        FetchRecentlyUsedPaymentMethodsUseCaseImpl(paymentRepository, json)
    }

    val fetchSavedUpiIdUseCase: FetchSavedUpiIdUseCase by lazy {
        FetchSavedUpiIdUseCaseImpl(paymentRepository)
    }

    val fetchEnabledPaymentMethodUseCase: FetchEnabledPaymentMethodUseCase by lazy {
        FetchEnabledPaymentMethodUseCaseImpl(paymentRepository)
    }

    val orderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase by lazy {
        FetchOrderStatusDynamicCardsUseCaseImpl(paymentRepository)
    }
}