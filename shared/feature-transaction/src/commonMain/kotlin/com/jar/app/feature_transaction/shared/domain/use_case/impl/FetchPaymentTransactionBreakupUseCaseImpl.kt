package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.FetchPaymentTransactionBreakupUseCase

internal class FetchPaymentTransactionBreakupUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : FetchPaymentTransactionBreakupUseCase {

    override suspend fun fetchPaymentTransactionBreakup(
        orderId: String?,
        type: String?
    ) = transactionRepository.fetchPaymentTransactionBreakup(orderId, type)

}