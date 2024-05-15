package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import kotlinx.coroutines.flow.Flow

interface FetchManualPaymentStatusUseCase {

    suspend fun fetchManualPaymentStatus(
        fetchManualPaymentRequest: FetchManualPaymentRequest,
        times: Int = 1, // One Means API will be called only once
        showLoading: () -> Unit = {},
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean = { false }
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>

}