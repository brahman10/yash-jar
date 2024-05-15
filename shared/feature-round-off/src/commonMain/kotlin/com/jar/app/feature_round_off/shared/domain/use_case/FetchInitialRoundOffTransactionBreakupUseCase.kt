package com.jar.app.feature_round_off.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp
import kotlinx.coroutines.flow.Flow

interface FetchInitialRoundOffTransactionBreakupUseCase {

    suspend fun fetchInitialRoundOffTransactionBreakup(
        orderId: String?,
        type: String?
    ): Flow<RestClientResult<ApiResponseWrapper<RoundOffBreakUp>>>

}