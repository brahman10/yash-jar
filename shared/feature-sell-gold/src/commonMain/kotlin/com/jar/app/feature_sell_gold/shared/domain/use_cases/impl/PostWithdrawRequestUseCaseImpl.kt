package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostWithdrawRequestUseCase

internal class PostWithdrawRequestUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IPostWithdrawRequestUseCase {
    override suspend fun invoke(request: WithdrawRequest) =
        repository.postGoldWithdrawalRequest(request)
}