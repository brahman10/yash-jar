package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectResponse
import kotlinx.coroutines.flow.Flow

interface InitiateUpiCollectUseCase {

    suspend fun initiateUpiCollect(initiateUpiCollectRequest: InitiateUpiCollectRequest): Flow<RestClientResult<ApiResponseWrapper<InitiateUpiCollectResponse>>>
}