package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.RetryPaymentRequest
import kotlinx.coroutines.flow.Flow

interface RetryPaymentUseCase {

    suspend fun retryPayment(retryPaymentRequest: RetryPaymentRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
}