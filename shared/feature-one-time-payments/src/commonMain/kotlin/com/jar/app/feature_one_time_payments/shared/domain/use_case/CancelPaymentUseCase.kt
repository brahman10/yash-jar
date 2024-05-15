package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface CancelPaymentUseCase {
    suspend fun cancelPayment(orderId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}