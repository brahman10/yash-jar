package com.jar.app.feature_lending.impl.ui.withdrawal_wait

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.withdrawal_wait.LendingWithdrawalWaitViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingWithdrawalWaitViewModelAndroid @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LendingWithdrawalWaitViewModel(
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            fetchReadyCashJourneyUseCase = fetchReadyCashJourneyUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}