package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.InitiateFailedPaymentsUseCase

internal class InitiateFailedPaymentsUseCaseImpl constructor(private val postSetupRepository: PostSetupRepository) :
    InitiateFailedPaymentsUseCase {
    override suspend fun initiatePaymentForFailedTransactions(
        amount: Float,
        paymentProvider: String,
        type: String,
        roundOffsLinked: List<String>
    ) = postSetupRepository.initiatePaymentForFailedTransactions(
        amount,
        paymentProvider,
        type,
        roundOffsLinked
    )
}