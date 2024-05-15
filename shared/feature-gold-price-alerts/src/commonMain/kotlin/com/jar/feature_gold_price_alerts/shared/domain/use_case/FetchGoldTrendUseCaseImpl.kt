package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository

internal class FetchGoldTrendUseCaseImpl constructor(private val repository: GoldPriceAlertsRepository) :
    FetchGoldTrendUseCase {

    override suspend fun fetchGoldPriceTrend(
        unit: String,
        period: Int
    ) = repository.fetchGoldPriceTrend(unit, period)
}