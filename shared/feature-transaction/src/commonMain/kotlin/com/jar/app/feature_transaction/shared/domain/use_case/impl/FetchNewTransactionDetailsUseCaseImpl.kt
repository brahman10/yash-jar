package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.FetchNewTransactionDetailsUseCase

internal class FetchNewTransactionDetailsUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
): FetchNewTransactionDetailsUseCase {
    override suspend fun fetchNewTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) = transactionRepository.fetchNewTxnDetails(orderId, assetSourceType, assetTxnId)
}