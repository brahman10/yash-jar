package com.jar.app.feature_one_time_payments.shared.data.repository

import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.*
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.RecentlyUsedPaymentMethodData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper

internal interface PaymentRepository : BaseRepository {

    suspend fun fetchManualPaymentStatus(
        fetchManualPaymentRequest: FetchManualPaymentRequest,
        times: Int,
        showLoading: () -> Unit,
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>

    suspend fun verifyUpiAddress(
        upiAddress: String,
        isEligibleForMandate: Boolean?
    ): Flow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse?>>>

    suspend fun initiateUpiCollect(initiateUpiCollectRequest: InitiateUpiCollectRequest): Flow<RestClientResult<ApiResponseWrapper<InitiateUpiCollectResponse>>>

    suspend fun retryPayment(retryPaymentRequest: RetryPaymentRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>

    suspend fun cancelPayment(orderId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchRecentlyUsedPaymentMethods(flowContext: String?): Flow<RestClientResult<ApiResponseWrapper<RecentlyUsedPaymentMethodData>>>

    suspend fun fetchSavedUpiIds(): Flow<RestClientResult<ApiResponseWrapper<SavedUpiIdsResponse>>>

    suspend fun fetchEnabledPaymentMethods(transactionType: String?): Flow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse>>>

    suspend fun fetchOrderStatusDynamicCards(
        orderType: DynamicCardsOrderType,
        orderId: String?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}