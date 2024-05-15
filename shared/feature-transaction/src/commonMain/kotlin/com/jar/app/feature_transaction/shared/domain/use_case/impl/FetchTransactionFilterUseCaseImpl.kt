package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionFilterUseCase

internal class FetchTransactionFilterUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchTransactionFilterUseCase {
    override suspend fun fetchTransactionFilters() = transactionRepository.fetchTransactionFilters()
}