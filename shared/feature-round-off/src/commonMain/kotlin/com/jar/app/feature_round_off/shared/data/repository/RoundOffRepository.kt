package com.jar.app.feature_round_off.shared.data.repository

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff
import com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_round_off.shared.domain.model.RoundOffStepsResp
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface RoundOffRepository : BaseRepository {

    suspend fun initialRoundOffsData(type: String): Flow<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>

    suspend fun fetchPaymentTransactionBreakup(
        orderId: String?,
        type: String?
    ): Flow<RestClientResult<ApiResponseWrapper<RoundOffBreakUp>>>

    suspend fun makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

    suspend fun fetchRoundOffSetupSteps(): Flow<RestClientResult<ApiResponseWrapper<RoundOffStepsResp>>>
}