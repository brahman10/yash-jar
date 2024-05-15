package com.jar.app.feature_round_off.shared.domain.repository

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_round_off.shared.data.network.RoundOffDataSource
import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest

internal class RoundOffRepositoryImpl constructor(
    private val roundOffDataSource: RoundOffDataSource
) : RoundOffRepository {

    override suspend fun initialRoundOffsData(type: String) = getFlowResult {
        roundOffDataSource.initialRoundOffsData(type)
    }

    override suspend fun fetchPaymentTransactionBreakup(
        orderId: String?,
        type: String?
    ) = getFlowResult {
        roundOffDataSource.fetchPaymentTransactionBreakup(orderId, type)
    }

    override suspend fun makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ) = getFlowResult {
        roundOffDataSource.makeDetectedSpendsPayment(
            initiateDetectedRoundOffsPaymentRequest,
            paymentGateway
        )
    }

    override suspend fun fetchRoundOffSetupSteps() = getFlowResult {
        roundOffDataSource.fetchRoundOffSetupSteps()
    }

}