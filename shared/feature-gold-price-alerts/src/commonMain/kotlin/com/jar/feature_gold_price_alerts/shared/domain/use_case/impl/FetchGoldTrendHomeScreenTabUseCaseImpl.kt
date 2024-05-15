package com.jar.feature_gold_price_alerts.shared.domain.use_case.impl

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendHomeScreenTabUseCase

class FetchGoldTrendHomeScreenTabUseCaseImpl constructor(
    private val repository: GoldPriceAlertsRepository
): FetchGoldTrendHomeScreenTabUseCase {

    override suspend fun fetchGoldTrendHomeScreenTab() =
        repository.fetchGoldTrendHomeScreenTab()

}