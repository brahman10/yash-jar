package com.jar.app.feature_round_off.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_round_off.shared.domain.model.RoundOffStepsResp
import kotlinx.coroutines.flow.Flow

interface FetchRoundOffStepsUseCase {
    suspend fun fetchRoundOffSetupSteps(): Flow<RestClientResult<ApiResponseWrapper<RoundOffStepsResp>>>
}