package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2StatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseStatusUseCase {

    suspend fun fetchGoldLeaseStatus(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2StatusResponse?>>>

}