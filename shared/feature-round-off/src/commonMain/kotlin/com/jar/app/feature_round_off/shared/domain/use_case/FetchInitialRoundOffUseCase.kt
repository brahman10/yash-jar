package com.jar.app.feature_round_off.shared.domain.use_case

import com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchInitialRoundOffUseCase {

    suspend fun initialRoundOffsData(type: String): Flow<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>

}