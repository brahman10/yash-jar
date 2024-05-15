package com.jar.app.feature_lending.shared.ui.repayments.overview

import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetailResponse
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.InitiateForeclosurePaymentUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RepaymentOverviewViewModel constructor(
    private val fetchRepaymentDetailsUseCase: FetchRepaymentDetailsUseCase,
    private val initiateForeclosurePaymentUseCase: InitiateForeclosurePaymentUseCase,
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _repaymentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RepaymentDetailResponse?>>>(
            RestClientResult.none()
        )
    val repaymentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<RepaymentDetailResponse?>>>
        get() = _repaymentFlow.toCommonStateFlow()

    private val _initiatePaymentResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiatePaymentResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _initiatePaymentResponseFlow.toCommonFlow()

    private val _preApprovedDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>(RestClientResult.none())
    val preApprovedDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>
        get() = _preApprovedDataFlow.toCommonStateFlow()

    private val _fetchManualPaymentResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseFlow.toCommonFlow()

    var paymentTitle = ""
    var orderId = ""

    fun fetchRepaymentDetails(loanId: String) {
        viewModelScope.launch {
            fetchRepaymentDetailsUseCase.getRepaymentDetails(loanId).collect {
                _repaymentFlow.emit(it)
            }
        }
    }

    fun initiatePayment(initiatePaymentRequest: InitiatePaymentRequest) {
        viewModelScope.launch {
            initiateForeclosurePaymentUseCase.initiateForeclosurePayment(initiatePaymentRequest)
                .collect {
                    _initiatePaymentResponseFlow.emit(it)
                }
        }
    }

    fun fetchPreApprovedData() {
        viewModelScope.launch {
            fetchLendingV2PreApprovedDataUseCase.fetchPreApprovedData().collect {
                _preApprovedDataFlow.emit(it)
            }
        }
    }

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