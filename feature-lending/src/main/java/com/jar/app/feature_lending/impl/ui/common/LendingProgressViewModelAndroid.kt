package com.jar.app.feature_lending.impl.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanApplicationsUseCase
import com.jar.app.feature_lending.shared.ui.common.LendingProgressViewModel
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This ViewModel can be used when we need to observe lending progress.
 * Note - LendingViewModel already have a liveData for the same but since LendingViewModel
 * is activity scoped throughout the flow and loanApplicationLiveData is
 * SingleLiveEvent which is observed in LendingStepsFragment for redirection purpose,
 * observing the same in other places is not appropriate
 **/
@HiltViewModel
internal class LendingProgressViewModelAndroid @Inject constructor(
    private val fetchLoanApplicationsUseCase: FetchLoanApplicationsUseCase,
    private val lendingStepsProgressGenerator: LendingStepsProgressGenerator
) : ViewModel() {

    private val viewModel by lazy {
        LendingProgressViewModel(
            fetchLoanApplicationsUseCase = fetchLoanApplicationsUseCase,
            lendingStepsProgressGenerator = lendingStepsProgressGenerator,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}