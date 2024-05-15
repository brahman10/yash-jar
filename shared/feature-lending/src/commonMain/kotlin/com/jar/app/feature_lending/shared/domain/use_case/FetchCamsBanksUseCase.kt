package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchCamsBanksUseCase {

    suspend fun fetchCamsBanks(): Flow<RestClientResult<ApiResponseWrapper<List<RealtimeBankData>?>>>

}