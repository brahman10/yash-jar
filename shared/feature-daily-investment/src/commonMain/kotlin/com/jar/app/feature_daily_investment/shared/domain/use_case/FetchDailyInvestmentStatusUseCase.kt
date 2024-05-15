package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import kotlinx.coroutines.flow.Flow

interface FetchDailyInvestmentStatusUseCase {

    suspend fun fetchDailyInvestmentStatus(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>
}