package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.app.feature_sell_gold.shared.domain.models.GoldSellOptionResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IFetchGoldSellOptionUseCase {
    suspend fun invoke(): Flow<RestClientResult<ApiResponseWrapper<GoldSellOptionResponse?>>>
}