package com.jar.app.feature_gold_price.shared.domain.repository

import com.jar.app.feature_gold_price.shared.data.model.GoldPriceContext
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.data.network.GoldPriceDataSource
import com.jar.app.feature_gold_price.shared.data.repository.GoldPriceRepository

internal class GoldPriceRepositoryImpl constructor(
    private val goldPriceDataSource: GoldPriceDataSource
) : GoldPriceRepository {

    override suspend fun fetchCurrentGoldPrice(goldPriceType: GoldPriceType, goldPriceContext: GoldPriceContext?) = getFlowResult {
        goldPriceDataSource.fetchCurrentGoldPrice(goldPriceType, goldPriceContext)
    }

    override suspend fun fetchCurrentGoldPriceSync(goldPriceType: GoldPriceType) =
        goldPriceDataSource.fetchCurrentGoldPrice(goldPriceType)
}