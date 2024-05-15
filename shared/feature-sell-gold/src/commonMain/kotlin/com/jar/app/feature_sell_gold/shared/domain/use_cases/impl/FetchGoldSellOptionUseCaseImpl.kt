package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchGoldSellOptionUseCase

internal class FetchGoldSellOptionUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : IFetchGoldSellOptionUseCase {
    override suspend fun invoke() = repository.fetchGoldSellOptionData()
}