package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.CancelPaymentUseCase

internal class CancelPaymentUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : CancelPaymentUseCase {

    override suspend fun cancelPayment(orderId: String) = paymentRepository.cancelPayment(orderId)
}