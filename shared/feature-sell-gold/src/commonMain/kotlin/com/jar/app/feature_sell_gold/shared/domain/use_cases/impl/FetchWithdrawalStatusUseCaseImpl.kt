package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalStatusUseCase

internal class FetchWithdrawalStatusUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IFetchWithdrawalStatusUseCase {

    override suspend fun fetchWithdrawalStatus(orderId: String) = repository.fetchWithdrawalStatus(orderId)

}