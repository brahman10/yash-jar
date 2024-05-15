package com.jar.app.feature_one_time_payments.shared.domain.repository

import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.network.PaymentDataSource
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.RetryPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

internal class PaymentRepositoryImpl constructor(
    private val paymentDataSource: PaymentDataSource
) : PaymentRepository {

    override suspend fun fetchManualPaymentStatus(
        fetchManualPaymentRequest: FetchManualPaymentRequest,
        times: Int,
        showLoading: () -> Unit,
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean
    ) =
        getFlowResult {
            paymentDataSource.fetchManualPaymentStatus(
                fetchManualPaymentRequest,
                times,
                showLoading,
                shouldRetry
            )
        }

    override suspend fun verifyUpiAddress(
        upiAddress: String,
        isEligibleForMandate: Boolean?
    ) = getFlowResult {
        paymentDataSource.verifyUpiAddress(upiAddress, isEligibleForMandate)
    }

    override suspend fun initiateUpiCollect(initiateUpiCollectRequest: InitiateUpiCollectRequest) =
        getFlowResult {
            paymentDataSource.initiateUpiCollect(initiateUpiCollectRequest)
        }

    override suspend fun retryPayment(retryPaymentRequest: RetryPaymentRequest) = getFlowResult {
        paymentDataSource.retryPayment(retryPaymentRequest)
    }

    override suspend fun cancelPayment(orderId: String) = getFlowResult {
        paymentDataSource.cancelPayment(orderId)
    }

    override suspend fun fetchRecentlyUsedPaymentMethods(flowContext: String?) = getFlowResult {
        paymentDataSource.fetchRecentlyUsedPaymentMethods(flowContext)
    }

    override suspend fun fetchSavedUpiIds() = getFlowResult {
        paymentDataSource.fetchSavedUpiIds()
    }

    override suspend fun fetchEnabledPaymentMethods(transactionType: String?) = getFlowResult {
        paymentDataSource.fetchEnabledPaymentMethods(transactionType)
    }

    override suspend fun fetchOrderStatusDynamicCards(
        orderType: DynamicCardsOrderType,
        orderId: String?
    ) = getFlowResult {
        paymentDataSource.fetchOrderStatusDynamicCards(orderType, orderId)
    }
}