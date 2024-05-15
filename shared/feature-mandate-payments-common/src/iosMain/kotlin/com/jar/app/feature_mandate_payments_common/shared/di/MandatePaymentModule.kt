package com.jar.app.feature_mandate_payments_common.shared.di

import com.jar.app.feature_mandate_payments_common.shared.data.network.MandatePaymentDataSource
import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.repository.MandatePaymentRepositoryImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchEnabledPaymentMethodsUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandateEducationUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchPreferredBankUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchEnabledPaymentMethodsUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchMandateEducationUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchMandatePaymentStatusUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.FetchPreferredBankUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.InitiateMandatePaymentUseCaseImpl
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl.VerifyUpiAddressUseCaseImpl
import io.ktor.client.HttpClient

class MandatePaymentModule(
    client: HttpClient
) {

    private val mandatePaymentDataSource: MandatePaymentDataSource by lazy {
        MandatePaymentDataSource(client)
    }

    private val mandatePaymentRepository: MandatePaymentRepository by lazy {
        MandatePaymentRepositoryImpl(mandatePaymentDataSource)
    }

    val verifyUpiAddressUseCase: VerifyUpiAddressUseCase by lazy {
        VerifyUpiAddressUseCaseImpl(mandatePaymentRepository)
    }

    val initiateMandatePaymentUseCase: InitiateMandatePaymentUseCase by lazy {
        InitiateMandatePaymentUseCaseImpl(mandatePaymentRepository)
    }

    val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase by lazy {
        FetchMandatePaymentStatusUseCaseImpl(mandatePaymentRepository)
    }

    val fetchMandateEducationUseCase: FetchMandateEducationUseCase by lazy {
        FetchMandateEducationUseCaseImpl(mandatePaymentRepository)
    }

    val fetchPreferredBankUseCase: FetchPreferredBankUseCase by lazy {
        FetchPreferredBankUseCaseImpl(mandatePaymentRepository)
    }

    val fetchEnabledMethodsUseCase: FetchEnabledPaymentMethodsUseCase by lazy {
        FetchEnabledPaymentMethodsUseCaseImpl(mandatePaymentRepository)
    }
}