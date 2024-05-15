package com.jar.app.feature_lending.shared.ui.kyc.confirm_kyc

import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
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

class ConfirmKycViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _kycInfoFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>()
    val kycInfoFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _kycInfoFlow.toCommonFlow()

    private val _kycConsentUpdateFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val kycConsentUpdateFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _kycConsentUpdateFlow.toCommonFlow()

    fun fetchKycInfo(loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(
                loanId,
                LendingConstants.LendingApplicationCheckpoints.KYC
            ).collect {
                _kycInfoFlow.emit(it)
            }
        }
    }

    fun updateKycConsent(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.KYC_CONSENT
            ).collect {
                _kycConsentUpdateFlow.emit(it)
            }
        }
    }
}