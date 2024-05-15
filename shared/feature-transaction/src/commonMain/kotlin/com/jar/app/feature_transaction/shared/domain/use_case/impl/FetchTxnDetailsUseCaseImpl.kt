package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTxnDetailsUseCase

internal class FetchTxnDetailsUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : IFetchTxnDetailsUseCase {
    override suspend fun fetchTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) =
        transactionRepository.fetchTxnDetails(orderId, assetSourceType, assetTxnId)
}