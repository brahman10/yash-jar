package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchKycDetailsForSellGoldUseCase

internal class FetchKycDetailsForSellGoldUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : FetchKycDetailsForSellGoldUseCase {
    override suspend operator fun invoke() = repository.fetchKycDetails()
}