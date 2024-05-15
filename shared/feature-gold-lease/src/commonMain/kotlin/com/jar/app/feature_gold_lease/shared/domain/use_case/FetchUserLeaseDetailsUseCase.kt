package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Details
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchUserLeaseDetailsUseCase {

    suspend fun fetchUserLeaseDetails(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Details?>>>

}