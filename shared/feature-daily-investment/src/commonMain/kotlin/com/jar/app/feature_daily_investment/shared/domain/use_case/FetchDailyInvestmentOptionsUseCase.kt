package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentOptionsResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDailyInvestmentOptionsUseCase {

    suspend fun fetchDailyInvestmentOptions(context: String? = null): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentOptionsResponse>>>

}