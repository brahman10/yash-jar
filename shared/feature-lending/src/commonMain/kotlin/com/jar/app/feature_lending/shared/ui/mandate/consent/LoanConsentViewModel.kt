package com.jar.app.feature_lending.shared.ui.mandate.consent

import com.jar.app.feature_lending.shared.domain.model.v2.ConsentDto
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.MandateDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.v2.getConsentData
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
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

class LoanConsentViewModel constructor(
    private val staticContentUseCase: FetchStaticContentUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val staticContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonStateFlow()

    var consentList: List<ConsentDto>? =null
    private val _consentFlow = MutableStateFlow<List<ConsentDto>>(emptyList())
    val consentFlow: CStateFlow<List<ConsentDto>>
        get() = _consentFlow.toCommonStateFlow()

    var currentAuthType = LendingConstants.MandateAuthType.DEBIT_CARD

    private val _updateMandateDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateMandateDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateMandateDetailsFlow.toCommonFlow()

    private val _loanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>(RestClientResult.none())
    val loanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonStateFlow()

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

    fun updateConsentList(position: Int, isChecked: Boolean) {
        viewModelScope.launch {
            consentList?.let { list ->
                val modifiedList = list.mapIndexed { index, consentDto ->
                    if (position == index) consentDto.copy(isSelected = isChecked)
                    else consentDto
                }
                _consentFlow.emit(modifiedList)
            }
        }
    }

    fun updateMandateConsent(loanId: String, currentAuthType: String,ipAddress:String?) {
        viewModelScope.launch {
            viewModelScope.launch {
                updateLoanDetailsV2UseCase.updateLoanDetails(
                    UpdateLoanDetailsBodyV2(
                        applicationId = loanId,
                        ipAddress=ipAddress,
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

    fun fetchLoanDetails(
        loanId: String
    ) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }
}