package com.jar.app.feature_one_time_payments.shared.data.network

import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.*
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.RetryPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.RecentlyUsedPaymentMethodData
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import io.ktor.client.*
import io.ktor.client.request.*

internal class PaymentDataSource constructor(
    private val client: HttpClient,
) : BaseDataSource() {

    suspend fun fetchManualPaymentStatus(
        fetchManualPaymentRequest: FetchManualPaymentRequest,
        times: Int,
        showLoading: () -> Unit,
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean
    ) = retryIOs(
        initialDelay = 6000L,
        factor = 1.0,
        times = times,
        block = {
            showLoading()
            getResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>{
                client.post {
                    url(Endpoints.FETCH_MANUAL_PAYMENT_STATUS)
                    setBody(fetchManualPaymentRequest)
                }
            }
        },
        shouldRetry = {
            shouldRetry.invoke(it)
        }
    )

    suspend fun verifyUpiAddress(
        upiAddress: String, isEligibleForMandate: Boolean?
    ) = getResult<ApiResponseWrapper<VerifyUpiAddressResponse?>> {
        client.post {
            url(Endpoints.VERIFY_VPA)
            parameter("upiAddress", upiAddress)
            if (isEligibleForMandate != null) parameter(
                "isEligibleForMandate", isEligibleForMandate
            )
        }
    }

    suspend fun initiateUpiCollect(initiateUpiCollectRequest: InitiateUpiCollectRequest) =
        getResult<ApiResponseWrapper<InitiateUpiCollectResponse>> {
            client.post {
                url(Endpoints.INITIATE_UPI_COLLECT)
                setBody(initiateUpiCollectRequest)
            }
        }

    suspend fun retryPayment(retryPaymentRequest: RetryPaymentRequest) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse>> {
            client.post {
                url(Endpoints.RETRY_PAYMENT)
                setBody(retryPaymentRequest)
            }
        }

    suspend fun cancelPayment(orderId: String) = getResult<ApiResponseWrapper<Unit?>> {
        client.get {
            url(Endpoints.CANCEL_PAYMENT)
            parameter("orderId", orderId)
        }
    }

    suspend fun fetchRecentlyUsedPaymentMethods(flowContext: String?) =
        getResult<ApiResponseWrapper<RecentlyUsedPaymentMethodData>> {
            client.get {
                url(Endpoints.FETCH_RECENTLY_USED_PAYMENT_METHODS)
                parameter("context", flowContext)
            }
        }

    suspend fun fetchSavedUpiIds() =
        getResult<ApiResponseWrapper<SavedUpiIdsResponse>> {
            client.get {
                url(Endpoints.FETCH_SAVED_VPA)
            }
        }

    suspend fun fetchEnabledPaymentMethods(transactionType: String?) =
        getResult<ApiResponseWrapper<EnabledPaymentMethodResponse>> {
            client.get {
                url(Endpoints.FETCH_ENABLED_PAYMENT_METHODS)
                if (transactionType.isNullOrBlank().not())
                    parameter("transactionType", transactionType)
            }
        }

    suspend fun fetchOrderStatusDynamicCards(orderType: DynamicCardsOrderType, orderId: String?) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.FETCH_ORDER_STATUS_DYNAMIC_CARDS)
                parameter("orderType", orderType.name)
                parameter("orderId", orderId)
            }
        }
}