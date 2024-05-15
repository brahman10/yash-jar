package com.jar.app.feature_lending.shared.ui.foreclosure

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ForeclosureStatusViewModel constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _fetchManualPaymentResponseFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>(
            RestClientResult.none()
        )
    val fetchManualPaymentResponseFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseFlow.toCommonStateFlow()

    fun fetchManualPaymentStatus(orderId: String, paymentProvider: String) {
        viewModelScope.launch {
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                FetchManualPaymentRequest(
                    orderId = orderId,
                    paymentProvider = paymentProvider,
                    transactionType = "LOAN_REPAYMENT"
                )
            ).collect {
                _fetchManualPaymentResponseFlow.emit(it)
            }
        }
    }
}