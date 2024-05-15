package com.jar.app.feature_lending.shared.ui.final_details

import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LoanFinalDetailsViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _loanDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>()
    val loanDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonFlow()


    fun fetchLoanDetails(loanId: String, checkPoint: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, checkPoint).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }
}