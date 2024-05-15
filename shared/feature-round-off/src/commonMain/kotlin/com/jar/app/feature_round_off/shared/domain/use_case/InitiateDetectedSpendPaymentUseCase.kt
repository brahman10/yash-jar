package com.jar.app.feature_round_off.shared.domain.use_case

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import kotlinx.coroutines.flow.Flow

interface InitiateDetectedSpendPaymentUseCase {

    suspend fun makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

}