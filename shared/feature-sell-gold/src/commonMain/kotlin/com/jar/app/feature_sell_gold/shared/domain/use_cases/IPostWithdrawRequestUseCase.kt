package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IPostWithdrawRequestUseCase {
    suspend fun invoke(request: WithdrawRequest): Flow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>
}