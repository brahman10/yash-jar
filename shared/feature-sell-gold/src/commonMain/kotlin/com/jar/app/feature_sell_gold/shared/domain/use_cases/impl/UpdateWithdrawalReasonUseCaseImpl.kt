package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IUpdateWithdrawalReasonUseCase

internal class UpdateWithdrawalReasonUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IUpdateWithdrawalReasonUseCase {

    override suspend fun updateWithdrawalReason(orderId: String, reason: String) = repository.updateWithdrawalReason(orderId, reason)

}