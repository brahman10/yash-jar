package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchInvestedAmntBreakupUseCase

internal class FetchInvestedAmntBreakupUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchInvestedAmntBreakupUseCase {
    override suspend fun fetchInvestedAmountBreakDown() =
        transactionRepository.fetchInvestedAmountBreakdown()
}