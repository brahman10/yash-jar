package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow


interface CartOrderUseCase {

    suspend fun submitFeedback(
        orderId: String,
        feedback: Int
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}

