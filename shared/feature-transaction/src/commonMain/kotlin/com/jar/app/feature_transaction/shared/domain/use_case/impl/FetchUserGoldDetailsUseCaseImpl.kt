package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserGoldDetailsUseCase

internal class FetchUserGoldDetailsUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchUserGoldDetailsUseCase {
    override suspend fun fetchUserGoldDetails() = transactionRepository.fetchUserGoldDetails()
}