package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateRequest
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface InitiateGoldLeaseV2UseCase {

    suspend fun initiateGoldLeaseV2(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2InitiateResponse?>>>

}