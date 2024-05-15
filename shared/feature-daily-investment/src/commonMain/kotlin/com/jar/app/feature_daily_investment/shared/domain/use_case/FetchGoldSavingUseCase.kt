package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.app.feature_one_time_payments_common.shared.PostPaymentReward
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldSavingUseCase {
    suspend fun fetchDailyInvestedGoldSaving(): Flow<RestClientResult<ApiResponseWrapper<PostPaymentReward?>>>
}