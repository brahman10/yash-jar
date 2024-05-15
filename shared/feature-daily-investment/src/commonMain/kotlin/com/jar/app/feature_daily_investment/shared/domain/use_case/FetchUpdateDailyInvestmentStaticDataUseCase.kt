package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.app.feature_daily_investment.shared.domain.model.UpdateDailyInvestmentStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchUpdateDailyInvestmentStaticDataUseCase {
    suspend fun fetchUpdateDailyInvestmentStaticData(): Flow<RestClientResult<ApiResponseWrapper<UpdateDailyInvestmentStaticData?>>>
}