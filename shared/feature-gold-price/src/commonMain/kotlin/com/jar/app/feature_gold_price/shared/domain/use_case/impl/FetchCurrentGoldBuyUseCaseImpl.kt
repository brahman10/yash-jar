package com.jar.app.feature_gold_price.shared.domain.use_case.impl

import com.jar.app.feature_gold_price.shared.data.model.GoldPriceContext
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.data.repository.GoldPriceRepository

internal class FetchCurrentGoldBuyUseCaseImpl constructor(
    private val goldPriceRepository: GoldPriceRepository
) :
    FetchCurrentGoldPriceUseCase {

    override suspend fun fetchCurrentGoldPrice(goldPriceType: GoldPriceType, goldPriceContext: GoldPriceContext?) = goldPriceRepository.fetchCurrentGoldPrice(goldPriceType, goldPriceContext)

    override suspend fun fetchCurrentGoldPriceSync(goldPriceType: GoldPriceType) =
        goldPriceRepository.fetchCurrentGoldPriceSync(goldPriceType)
}