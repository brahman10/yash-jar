package com.jar.app.feature_transaction.shared.domain.use_case.impl

import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_transaction.shared.domain.repository.TransactionRepository
import com.jar.app.feature_transaction.shared.domain.use_case.PostTransactionActionUseCase

internal class PostTransactionActionUseCaseImpl constructor(
    private val transactionRepository: TransactionRepository
) : PostTransactionActionUseCase {

    override suspend fun postTransactionAction(
        type: TransactionActionType,
        orderId: String,
        vpa: String
    ) = transactionRepository.postTransactionAction(orderId, type, vpa)
}