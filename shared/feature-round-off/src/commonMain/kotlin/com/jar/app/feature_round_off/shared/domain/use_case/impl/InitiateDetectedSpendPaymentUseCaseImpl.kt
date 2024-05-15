package com.jar.app.feature_round_off.shared.domain.use_case.impl

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.use_case.InitiateDetectedSpendPaymentUseCase

internal class InitiateDetectedSpendPaymentUseCaseImpl constructor(
    private val roundOffRepository: RoundOffRepository
) : InitiateDetectedSpendPaymentUseCase {

    override suspend fun makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ) = roundOffRepository.makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest,
        paymentGateway
    )

}