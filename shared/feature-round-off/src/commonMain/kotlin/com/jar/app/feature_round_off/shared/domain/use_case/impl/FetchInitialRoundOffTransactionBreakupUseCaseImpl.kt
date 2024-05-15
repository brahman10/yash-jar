package com.jar.app.feature_round_off.shared.domain.use_case.impl

import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffTransactionBreakupUseCase

internal class FetchInitialRoundOffTransactionBreakupUseCaseImpl constructor(
    private val roundOffRepository: RoundOffRepository
) : FetchInitialRoundOffTransactionBreakupUseCase {

    override suspend fun fetchInitialRoundOffTransactionBreakup(
        orderId: String?,
        type: String?
    ) = roundOffRepository.fetchPaymentTransactionBreakup(orderId, type)

}