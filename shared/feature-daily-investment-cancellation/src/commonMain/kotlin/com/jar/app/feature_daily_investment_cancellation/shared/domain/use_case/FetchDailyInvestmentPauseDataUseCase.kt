package com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case

import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDailyInvestmentPauseDataUseCase {
    suspend fun fetchDailyInvestmentPauseData(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>
}