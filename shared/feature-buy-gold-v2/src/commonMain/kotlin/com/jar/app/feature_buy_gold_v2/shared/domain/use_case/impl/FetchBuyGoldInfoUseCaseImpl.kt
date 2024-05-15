package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldInfoUseCase

internal class FetchBuyGoldInfoUseCaseImpl constructor(
    private val buyGoldRepository: BuyGoldV2Repository
) : FetchBuyGoldInfoUseCase {

    override suspend fun fetchBuyGoldInfo() = buyGoldRepository.fetchBuyGoldInfo()
}