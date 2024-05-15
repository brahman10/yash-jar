package com.jar.app.feature_round_off.shared.data.network

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp
import com.jar.app.feature_round_off.shared.util.RoundOffConstants.Endpoints
import com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_round_off.shared.domain.model.RoundOffStepsResp
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class RoundOffDataSource constructor(
    private val client: HttpClient,
) : BaseDataSource() {

    suspend fun initialRoundOffsData(type: String) =
        getResult<ApiResponseWrapper<InitialRoundOff?>> {
            client.get {
                url(Endpoints.FETCH_INITIAL_ROUND_OFF_DATA)
                parameter("type", type)
            }
        }

    suspend fun fetchPaymentTransactionBreakup(orderId: String?, type: String?) =
        getResult<ApiResponseWrapper<RoundOffBreakUp>> {
            client.get {
                url(Endpoints.FETCH_PAYMENT_TRANSACTION_BREAKUP)
                if (orderId.isNullOrBlank().not())
                    parameter("orderId", orderId)
                if (type.isNullOrBlank().not())
                    parameter("type", type)
            }
        }

    suspend fun makeDetectedSpendsPayment(
        initiateDetectedRoundOffsPaymentRequest: InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ) = getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
        client.get {
            url(Endpoints.MAKE_DETECTED_SPEND_PAYMENT)
            parameter("txnAmt", initiateDetectedRoundOffsPaymentRequest.txnAmt)
            parameter("orderId", initiateDetectedRoundOffsPaymentRequest.orderId)
            parameter("paymentProvider", paymentGateway.name)

            if (initiateDetectedRoundOffsPaymentRequest.percent != null)
                parameter("percent", initiateDetectedRoundOffsPaymentRequest.percent)

            if (initiateDetectedRoundOffsPaymentRequest.isPartial.orFalse())
                parameter("isPartial", initiateDetectedRoundOffsPaymentRequest.isPartial)

            if (initiateDetectedRoundOffsPaymentRequest.skip.orFalse())
                parameter("skip", initiateDetectedRoundOffsPaymentRequest.skip)
        }
    }

    suspend fun fetchRoundOffSetupSteps() =
        getResult<ApiResponseWrapper<RoundOffStepsResp>> {
            client.get {
                url(Endpoints.FETCH_ROUND_OFF_SETUP_STEPS)
                parameter("contentType", BaseConstants.StaticContentType.ROUND_OFF_STEPS.name)
            }
        }
}