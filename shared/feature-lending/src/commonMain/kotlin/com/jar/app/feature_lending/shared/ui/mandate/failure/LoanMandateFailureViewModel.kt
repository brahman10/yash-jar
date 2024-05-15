package com.jar.app.feature_lending.shared.ui.mandate.failure

import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.MandateDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
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

class LoanMandateFailureViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticContentFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>()
    val staticContentFlow: CFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonFlow()

    private val _updateMandateDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateMandateDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateMandateDetailsFlow.toCommonFlow()

    fun fetchStaticContent(loanId: String, contentType: String) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(loanId, contentType).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    fun updateMandateConsent(loanId: String, currentAuthType: String) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                UpdateLoanDetailsBodyV2(
                    applicationId = loanId,
                    mandateDetails = MandateDetailsV2(
                        mandateAuthType = currentAuthType,
                        mandateLink = null,
                        provider = null,
                        status = null
                    )
                ),
                LendingConstants.LendingApplicationCheckpoints.MANDATE_SETUP
            ).collect {
                _updateMandateDetailsFlow.emit(it)
            }
        }
    }
}