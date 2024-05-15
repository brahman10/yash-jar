package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseLandingDetailsUseCase {
    suspend fun fetchGoldLeaseLandingDetails(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseLandingDetails?>>>
}