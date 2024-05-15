package com.jar.app.feature_lending.shared.ui.repayments.payment

import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTransactionDetails
import com.jar.app.feature_lending.shared.domain.use_case.FetchTransactionDetailsUseCase
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

class RepaymentTxnDetailViewModel constructor(
    private val fetchTransactionDetailsUseCase: FetchTransactionDetailsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _transactionDetailFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RepaymentTransactionDetails?>>>(
            RestClientResult.none()
        )
    val transactionDetailFlow: CStateFlow<RestClientResult<ApiResponseWrapper<RepaymentTransactionDetails?>>>
        get() = _transactionDetailFlow.toCommonStateFlow()


    fun fetchTransactionDetail(paymentId: String) {
        viewModelScope.launch {
            fetchTransactionDetailsUseCase.getTransactionDetails(paymentId).collect {
                _transactionDetailFlow.emit(it)
            }
        }
    }
}