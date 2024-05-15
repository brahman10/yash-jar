package com.jar.app.feature_lending.shared.ui.foreclosure

import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
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

class ForeclosureSuccessViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _loanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>(RestClientResult.none())
    val loanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonStateFlow()

    fun fetchLoanDetails(
        checkPoint: String,
        shouldPassCheckpoint: Boolean = false,
        loanId: String
    ) {
        viewModelScope.launch {
            val cp = if (shouldPassCheckpoint) checkPoint else null
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, cp).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }
}