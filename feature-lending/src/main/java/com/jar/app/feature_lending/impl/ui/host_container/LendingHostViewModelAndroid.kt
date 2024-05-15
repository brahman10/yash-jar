package com.jar.app.feature_lending.impl.ui.host_container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.host_container.LendingHostViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingHostViewModelAndroid @Inject constructor(
    private val stepsProgressGenerator: LendingStepsProgressGenerator,
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchLoanApplicationListUseCase: FetchLoanApplicationListUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchLoanProgressStatusV2UseCase: FetchLoanProgressStatusV2UseCase,
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingHostViewModel(
            stepsProgressGenerator,
            fetchLendingV2PreApprovedDataUseCase,
            fetchLoanApplicationListUseCase,
            fetchLoanDetailsV2UseCase,
            fetchStaticContentUseCase,
            fetchLoanProgressStatusV2UseCase,
            fetchReadyCashJourneyUseCase,
            updateLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}