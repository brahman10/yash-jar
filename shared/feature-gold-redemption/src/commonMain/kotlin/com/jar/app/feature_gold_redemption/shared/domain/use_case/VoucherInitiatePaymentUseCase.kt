package com.jar.app.feature_gold_redemption.shared.domain.use_case

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface VoucherInitiatePaymentUseCase {
    suspend fun initiatePayment(
        tnxAmt: String,
        orderId: String
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentRequest?>>>
}