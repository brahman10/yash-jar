package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase

internal class FetchEnabledPaymentMethodUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : FetchEnabledPaymentMethodUseCase {

    override suspend fun fetchEnabledPaymentMethods(transactionType: String?) =
        paymentRepository.fetchEnabledPaymentMethods(transactionType)
}