package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository

class FetchGoldPriceTrendBottomSheetStaticDataUseCaseImpl constructor(private val repository: GoldPriceAlertsRepository) :
    FetchGoldPriceTrendBottomSheetStaticDataUseCase {
    override suspend fun fetchGoldPriceTrendBottomSheetStaticData() =
        repository.fetchGoldPriceTrendBottomSheetStaticData()
}
