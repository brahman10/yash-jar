package com.jar.feature_gold_price_alerts.shared.domain.use_case.impl

import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.use_case.DisableGoldPriceAlertUseCase

class DisableGoldPriceAlertUseCaseImpl constructor(private val repository: GoldPriceAlertsRepository) :
    DisableGoldPriceAlertUseCase {
    override suspend fun disableGoldPriceAlert(alertId: String) =
        repository.disableGoldPriceAlert(alertId)
}