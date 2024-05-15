package com.jar.feature_gold_price_alerts.shared.domain.use_case.impl

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase

class GetLatestGoldPriceAlertUseCaseImpl constructor(private val repository: GoldPriceAlertsRepository) :
    GetLatestGoldPriceAlertUseCase {
    override suspend fun getLatestGoldPriceAlert() = repository.getLatestGoldPriceAlert()
}
