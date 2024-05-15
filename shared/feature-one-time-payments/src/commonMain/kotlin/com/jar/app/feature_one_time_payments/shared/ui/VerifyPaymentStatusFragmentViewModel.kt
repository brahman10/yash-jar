package com.jar.app.feature_one_time_payments.shared.ui

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class VerifyPaymentStatusFragmentViewModel constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _fetchManualPaymentStatusFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentStatusFlow.toCommonFlow()

    fun fetchManualPaymentStatus(oneTimePaymentResult: OneTimePaymentResult) {
        viewModelScope.launch {
            val provider = oneTimePaymentResult.oneTimePaymentGateway
            val fetchManualPaymentRequest = FetchManualPaymentRequest(
                paymentProvider = provider.name,
                orderId = oneTimePaymentResult.orderId,
                juspay = oneTimePaymentResult.juspayPaymentResponse,
                transactionType = oneTimePaymentResult.transactionType
            )
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(fetchManualPaymentRequest)
                .collect {
                    _fetchManualPaymentStatusFlow.emit(it)
                }
        }
    }
}