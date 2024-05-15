package com.jar.app.feature_lending.shared.ui.repayments.payment

import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetailResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase
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

class PaymentDetailViewModel constructor(
    private val fetchRepaymentDetailsUseCase: FetchRepaymentDetailsUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _repaymentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RepaymentDetailResponse?>>>(
            RestClientResult.none()
        )
    val repaymentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<RepaymentDetailResponse?>>>
        get() = _repaymentFlow.toCommonStateFlow()

    fun fetchRepaymentDetails(loanId: String) {
        viewModelScope.launch {
            fetchRepaymentDetailsUseCase.getRepaymentDetails(loanId).collect {
                _repaymentFlow.emit(it)
            }
        }
    }
}