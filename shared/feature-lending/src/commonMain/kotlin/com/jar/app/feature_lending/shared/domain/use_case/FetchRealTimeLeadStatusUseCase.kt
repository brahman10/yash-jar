package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLeadStatus
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchRealTimeLeadStatusUseCase {

    suspend fun fetchRealTimeLeadStatus(): Flow<RestClientResult<ApiResponseWrapper<RealTimeLeadStatus?>>>

}