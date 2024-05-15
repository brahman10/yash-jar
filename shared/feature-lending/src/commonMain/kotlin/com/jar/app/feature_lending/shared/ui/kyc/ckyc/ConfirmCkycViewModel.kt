package com.jar.app.feature_lending.shared.ui.kyc.ckyc

import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ConfirmCkycViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _ckycInfoFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>()
    val ckycInfoFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _ckycInfoFlow.toCommonFlow()

    private val _ckycConsentUpdateFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val ckycConsentUpdateFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _ckycConsentUpdateFlow.toCommonFlow()

    fun fetchKycInfo(loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(
                loanId,
                LendingConstants.LendingApplicationCheckpoints.CKYC
            ).collect {
                _ckycInfoFlow.emit(it)
            }
        }
    }

    fun updateCkycConsent(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.CKYC
            ).collect {
                _ckycConsentUpdateFlow.emit(it)
            }
        }
    }
}