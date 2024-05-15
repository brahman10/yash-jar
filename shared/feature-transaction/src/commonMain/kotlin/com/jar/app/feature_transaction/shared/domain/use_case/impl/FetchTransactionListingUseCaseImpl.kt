package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionListingUseCase

internal class FetchTransactionListingUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchTransactionListingUseCase {
    override suspend fun fetchTransactionListing(request: com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest) =
        transactionRepository.getTransactionListingPaginated(request)
}