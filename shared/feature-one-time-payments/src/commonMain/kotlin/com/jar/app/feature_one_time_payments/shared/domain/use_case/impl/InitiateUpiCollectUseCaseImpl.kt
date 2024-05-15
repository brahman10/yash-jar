package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.InitiateUpiCollectUseCase

internal class InitiateUpiCollectUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : InitiateUpiCollectUseCase {

    override suspend fun initiateUpiCollect(initiateUpiCollectRequest: InitiateUpiCollectRequest) =
        paymentRepository.initiateUpiCollect(initiateUpiCollectRequest)
}