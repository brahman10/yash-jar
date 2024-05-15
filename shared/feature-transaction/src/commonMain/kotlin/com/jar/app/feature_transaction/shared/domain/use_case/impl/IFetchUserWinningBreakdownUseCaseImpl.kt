package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningBreakdownUseCase

class IFetchUserWinningBreakdownUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchUserWinningBreakdownUseCase {
    override suspend fun fetchUserWinningBreakdown() =
        transactionRepository.fetchUserWinningsBreakDown()
}