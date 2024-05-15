package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningDetailsUseCase

internal class FetchUserWinningDetailsUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchUserWinningDetailsUseCase {
    override suspend fun fetchUserWinningDetails() = transactionRepository.fetchUserWinningDetails()
}