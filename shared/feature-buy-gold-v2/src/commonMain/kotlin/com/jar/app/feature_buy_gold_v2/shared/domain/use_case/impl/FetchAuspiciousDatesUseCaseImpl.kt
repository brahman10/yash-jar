package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousDatesUseCase

internal class FetchAuspiciousDatesUseCaseImpl constructor(
    private val buyGoldV2Repository: BuyGoldV2Repository
) : FetchAuspiciousDatesUseCase {

    override suspend fun fetchAuspiciousDates(
        page: Int,
        size: Int
    ) = buyGoldV2Repository.fetchAuspiciousDates(page, size)

}