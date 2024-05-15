package com.jar.app.feature_lending.shared.ui.bank.confirm_bank

import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
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

class ConfirmBankDetailViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticContentFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>()
    val staticContentFlow: CFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonFlow()

    private val _loanDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>()
    val loanDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonFlow()

    private val _updateResponseFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateResponseFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateResponseFlow.toCommonFlow()

    var bankAccount: BankAccount? = null
    fun fetchBankDetail(loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsV2UseCase.getLoanDetails(
                loanId,
                LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS
            ).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }

    fun fetchStaticContent(loanId: String, contentType: String) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(loanId, contentType).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    fun confirmBankDetails(loanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                loanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS
            ).collect {
                _updateResponseFlow.emit(it)
            }
        }
    }
}