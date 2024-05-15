package com.jar.app.feature_lending.shared.ui.withdrawal_wait

import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
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

class LendingWithdrawalWaitViewModel constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _makeWithdrawalFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>(
            RestClientResult.none()
        )
    val makeWithdrawalFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _makeWithdrawalFlow.toCommonStateFlow()

    private val _readyCashJourneyFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>()
    val readyCashJourneyFlow: CFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>
        get() = _readyCashJourneyFlow.toCommonFlow()

    fun makeWithdrawal(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch(Dispatchers.Default) {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.WITHDRAWAL
            ).collect {
                _makeWithdrawalFlow.emit(it)
            }
        }
    }

    fun fetchLendingProgress() {
        viewModelScope.launch {
            fetchReadyCashJourneyUseCase.getReadyCashJourney().collect {
                _readyCashJourneyFlow.emit(it)
            }
        }
    }
}