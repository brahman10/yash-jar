package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.ExpandableFaqResponse
import kotlinx.coroutines.flow.Flow
interface FetchDailySavingsFaqDataUseCase {
    suspend fun fetchDailySavingsFaqData(): Flow<RestClientResult<ApiResponseWrapper<ExpandableFaqResponse>>>
}