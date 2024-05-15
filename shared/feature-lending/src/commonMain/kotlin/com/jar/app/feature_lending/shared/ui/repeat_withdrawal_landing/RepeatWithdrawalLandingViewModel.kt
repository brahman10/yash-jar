package com.jar.app.feature_lending.shared.ui.repeat_withdrawal_landing

import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationItemV2
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
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

class RepeatWithdrawalLandingViewModel constructor(
    private val fetchPreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchLoanApplicationListUseCase: FetchLoanApplicationListUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _preApprovedDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>(RestClientResult.none())
    val preApprovedDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>
        get() = _preApprovedDataFlow.toCommonStateFlow()

    private val _loanApplicationsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>(
            RestClientResult.none()
        )
    val loanApplicationsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>
        get() = _loanApplicationsFlow.toCommonStateFlow()

    var preApprovedData: PreApprovedData? = null
    var loanId: String? = null
    fun fetchPreApproved() {
        viewModelScope.launch {
            fetchPreApprovedDataUseCase.fetchPreApprovedData().collect {
                _preApprovedDataFlow.emit(it)
            }
        }
    }

    fun fetchLoanApplicationList() {
        viewModelScope.launch {
            fetchLoanApplicationListUseCase.fetchLoanApplicationList().collect {
                _loanApplicationsFlow.emit(it)
            }
        }
    }
}