package com.jar.app.feature_settings.domain.use_case

import com.jar.app.feature_settings.domain.model.DailyInvestmentCancellationV2RedirectionDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DailyInvestmentCancellationV2RedirectionDetailsUseCase {
    suspend fun fetchDailySavingRedirectionDetails(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentCancellationV2RedirectionDetails>>>
}