package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository

class FetchGoldPriceTrendScreenStaticUseCaseImpl constructor(private val goldPriceRepository: GoldPriceAlertsRepository) :
    FetchGoldPriceTrendScreenStaticUseCase {
    override suspend fun fetchGoldPriceTrendScreenStatic() =
        goldPriceRepository.fetchGoldPriceTrendScreenStatic()
}
