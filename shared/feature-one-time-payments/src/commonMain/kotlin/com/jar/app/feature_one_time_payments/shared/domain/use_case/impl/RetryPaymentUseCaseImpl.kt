package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.model.RetryPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.RetryPaymentUseCase

internal class RetryPaymentUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : RetryPaymentUseCase {

    override suspend fun retryPayment(retryPaymentRequest: RetryPaymentRequest) =
        paymentRepository.retryPayment(retryPaymentRequest)
}