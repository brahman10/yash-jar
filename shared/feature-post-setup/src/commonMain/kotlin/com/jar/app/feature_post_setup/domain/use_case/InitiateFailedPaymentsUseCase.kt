package com.jar.app.feature_post_setup.domain.use_case

import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface InitiateFailedPaymentsUseCase {

    suspend fun initiatePaymentForFailedTransactions(
        amount: Float,
        paymentProvider: String,
        type: String,
        roundOffsLinked: List<String>
    ): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>

}