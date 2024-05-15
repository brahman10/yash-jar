package com.jar.app.feature_lending.shared.ui.common

import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplications
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanApplicationsUseCase
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
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

/**
 * This ViewModel can be used when we need to observe lending progress.
 * Note - LendingViewModel already have a liveData for the same but since LendingViewModel
 * is activity scoped throughout the flow and loanApplicationLiveData is
 * SingleLiveEvent which is observed in LendingStepsFragment for redirection purpose,
 * observing the same in other places is not appropriate
 **/

class LendingProgressViewModel constructor(
    private val fetchLoanApplicationsUseCase: FetchLoanApplicationsUseCase,
    private val lendingStepsProgressGenerator: LendingStepsProgressGenerator,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _loanApplicationsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanApplications?>>>(RestClientResult.none())
    val loanApplicationsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanApplications?>>>
        get() = _loanApplicationsFlow.toCommonStateFlow()

    private val _lendingStepFlow = MutableSharedFlow<List<LendingProgressStep>>()
    val lendingStepFlow: CFlow<List<LendingProgressStep>>
        get() = _lendingStepFlow.toCommonFlow()

    var loanApplications: LoanApplications? = null

    var currentStep: LendingProgressStep? = null

    fun fetchLendingProgress(suppressRedirection: Boolean = false) {
        viewModelScope.launch {
            fetchLoanApplicationsUseCase.fetchLoanApplications().collect {
                loanApplications = it.data?.data
                _loanApplicationsFlow.emit(it)
            }
        }
    }
}