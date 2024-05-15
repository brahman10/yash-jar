package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.InvestWinningInGoldUseCase

internal class InvestWinningInGoldUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : InvestWinningInGoldUseCase {

    override suspend fun investWinningInGold(investWinningInGoldRequest: com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest) = transactionRepository.investWinningInGold(investWinningInGoldRequest)
}