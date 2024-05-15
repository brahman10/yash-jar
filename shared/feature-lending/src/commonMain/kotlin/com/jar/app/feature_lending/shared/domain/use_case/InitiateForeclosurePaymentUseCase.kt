package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface InitiateForeclosurePaymentUseCase {

    suspend fun initiateForeclosurePayment(initiatePaymentRequest: InitiatePaymentRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

}