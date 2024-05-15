package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPostSetupTransactionDetailsUseCase

class FetchPostSetupTransactionDetailsUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : FetchPostSetupTransactionDetailsUseCase {

    override suspend fun fetchPostSetupTransactionDetails(id: String) =
        transactionRepository.fetchPostSetupTransactionDetails(id)

}