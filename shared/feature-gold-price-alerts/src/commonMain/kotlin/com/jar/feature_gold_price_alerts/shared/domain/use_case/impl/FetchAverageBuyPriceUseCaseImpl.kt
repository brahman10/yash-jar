package com.jar.feature_gold_price_alerts.shared.domain.use_case.impl

import com.jar.feature_gold_price_alerts.shared.domain.model.CreateAlertRequest
import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.use_case.CreateGoldPriceAlertUseCase


class CreateGoldPriceAlertUseCaseImpl constructor(private val repository: GoldPriceAlertsRepository) :
    CreateGoldPriceAlertUseCase {
    override suspend fun createGoldPriceAlert(body: CreateAlertRequest) =
        repository.createGoldPriceAlert(body)
}
