package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostTransactionActionUseCase

internal class PostTransactionActionUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IPostTransactionActionUseCase {
    override suspend fun postTransactionAction(type: TransactionActionType, orderId: String, vpa: String) =
        repository.postTransactionAction(orderId, type, vpa)
}