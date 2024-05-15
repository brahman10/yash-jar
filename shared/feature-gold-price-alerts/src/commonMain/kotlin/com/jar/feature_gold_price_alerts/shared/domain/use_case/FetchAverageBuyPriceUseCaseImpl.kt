package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.feature_gold_price_alerts.shared.domain.model.AverageBuyPrice
import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchAverageBuyPriceUseCaseImpl constructor(
    private val repository: GoldPriceAlertsRepository
) : FetchAverageBuyPriceUseCase {
    override suspend fun fetchAverageBuyPrice(): Flow<RestClientResult<ApiResponseWrapper<AverageBuyPrice>>> =
        repository.fetchAverageBuyPrice()
}


