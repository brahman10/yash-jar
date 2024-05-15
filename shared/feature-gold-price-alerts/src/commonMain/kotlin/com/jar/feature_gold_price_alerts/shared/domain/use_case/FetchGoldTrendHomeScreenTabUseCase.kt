package com.jar.feature_gold_price_alerts.shared.domain.use_case

import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendHomeScreenTab
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldTrendHomeScreenTabUseCase {

    suspend fun fetchGoldTrendHomeScreenTab(): Flow<RestClientResult<ApiResponseWrapper<GoldTrendHomeScreenTab>>>

}