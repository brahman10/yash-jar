package com.jar.app.feature_lending.shared.ui.agreement

import com.jar.app.feature_lending.shared.domain.model.temp.LendingAgreementResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingAgreementUseCase
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

class LoanSummaryViewModel constructor(
    private val lendingAgreementUseCase: FetchLendingAgreementUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _loanAgreementFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LendingAgreementResponse?>>>(
            RestClientResult.none()
        )
    val loanAgreementFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LendingAgreementResponse?>>>
        get() = _loanAgreementFlow.toCommonStateFlow()

    fun fetchLoanAgreement(loanId: String) {
        viewModelScope.launch {
            lendingAgreementUseCase.fetchLendingAgreement(loanId).collect {
                _loanAgreementFlow.emit(it)
            }
        }
    }
}