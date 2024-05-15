package com.jar.app.feature_lending.shared.ui.mandate.status

import com.jar.app.feature_lending.shared.domain.model.temp.MandateData
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
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

class MandateViewModel constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val staticContentUseCase: FetchStaticContentUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var currentState = MANDATE_STATE_REDIRECTION

    private val _mandateFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<MandateData>>>(RestClientResult.none())
    val mandateFlow: CStateFlow<RestClientResult<ApiResponseWrapper<MandateData>>>
        get() = _mandateFlow.toCommonStateFlow()

    private val _loanApplicationsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>()
    val loanApplicationsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanApplicationsFlow.toCommonFlow()

    private val _staticContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val staticContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonStateFlow()

    fun fetchLendingProgress(loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(
                loanId,
                LendingConstants.LendingApplicationCheckpoints.MANDATE_SETUP
            ).collect {
                _loanApplicationsFlow.emit(it)
            }
        }
    }

    fun fetchStaticContent(loanId: String) {
        viewModelScope.launch {
            staticContentUseCase.fetchLendingStaticContent(
                loanId,
                LendingConstants.StaticContentType.MANDATE_SETUP_UPDATED_CONTENT
            ).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    companion object {
        const val MANDATE_STATE_REDIRECTION = "MANDATE_STATE_REDIRECTION"
        const val MANDATE_STATE_VERIFYING = "MANDATE_STATE_VERIFYING"
    }
}