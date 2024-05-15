package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchWinningListingUseCase

internal class FetchWinningListingUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchWinningListingUseCase {
    override suspend fun fetchWinningListing(pageNo: Int, pageSize: Int) =
        transactionRepository.getWinningListingPaginated(pageNo, pageSize)
}