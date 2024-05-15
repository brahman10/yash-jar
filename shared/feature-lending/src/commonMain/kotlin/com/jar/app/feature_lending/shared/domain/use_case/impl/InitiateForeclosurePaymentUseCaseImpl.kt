package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.use_case.InitiateForeclosurePaymentUseCase

internal class InitiateForeclosurePaymentUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : InitiateForeclosurePaymentUseCase {
    override suspend fun initiateForeclosurePayment(initiatePaymentRequest: InitiatePaymentRequest) =
        lendingRepository.initiateForeclosurePayment(initiatePaymentRequest)
}