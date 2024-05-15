package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DisableGoldPriceAlertUseCase {
    suspend fun disableGoldPriceAlert(alertId: String): Flow<RestClientResult<ApiResponseWrapper<Unit>>>
}
