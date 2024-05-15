package com.jar.app.feature_lending.shared.ui.bank.penny_drop

import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
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

class PennyDropViewModel constructor(
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _readyCashJourneyFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>()
    val readyCashJourneyFlow: CFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>
        get() = _readyCashJourneyFlow.toCommonFlow()

    private val _bankVerificationFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val bankVerificationFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _bankVerificationFlow.toCommonFlow()

    fun fetchReadyCashProgress() {
        viewModelScope.launch {
            fetchReadyCashJourneyUseCase.getReadyCashJourney().collect {
                _readyCashJourneyFlow.emit(it)
            }
        }
    }

    fun makeBankVerification(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.BANK_VERIFICATION
            ).collect {
                _bankVerificationFlow.emit(it)
            }
        }
    }
}