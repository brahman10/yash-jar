package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentMandateBottomSheetData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDSMandateDataUseCase {

    suspend fun fetchDSMandateBSData(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentMandateBottomSheetData?>>>
}