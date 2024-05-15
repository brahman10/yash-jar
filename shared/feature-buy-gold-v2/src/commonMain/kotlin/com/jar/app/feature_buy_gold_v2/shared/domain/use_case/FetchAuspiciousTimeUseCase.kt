package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTimeResponse
import kotlinx.coroutines.flow.Flow

interface FetchAuspiciousTimeUseCase {
    suspend fun fetchIsAuspiciousTime(): Flow<RestClientResult<ApiResponseWrapper<AuspiciousTimeResponse>>>
}